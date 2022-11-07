package com.practice.overviewspringsecurity.service;

import com.practice.overviewspringsecurity.Enum.EnumRole;
import com.practice.overviewspringsecurity.config.RandomPassword;
import com.practice.overviewspringsecurity.entity.ConfirmationToken;
import com.practice.overviewspringsecurity.entity.Role;
import com.practice.overviewspringsecurity.entity.User;
import com.practice.overviewspringsecurity.repository.ConfirmationTokenRepository;
import com.practice.overviewspringsecurity.repository.UserRepository;
import com.practice.overviewspringsecurity.request.SignUpRequest;
import com.practice.overviewspringsecurity.validation.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationService {

  private final ConfirmationTokenService confirmationTokenService;
  private final EmailService emailService;
  private final EmailValidator emailValidator;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final RandomPassword randomPassword;
  private final ConfirmationTokenRepository confirmationTokenRepository;

  public String register(SignUpRequest signUpRequest) {

    boolean isValidEmail = emailValidator.test(signUpRequest.getEmail());

    //Check email is valid
    if (!isValidEmail) {
      throw new IllegalStateException("Email is not valid!");
      //Check if username exits and email not exits
    } else if (userRepository.existsByUsername(signUpRequest.getUsername())
        && !userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new IllegalStateException("Username was exists in database!");
      //Check if username and email exists
    } else if ((userRepository.existsByEmail(signUpRequest.getEmail()) &&
        userRepository.existsByUsername(signUpRequest.getUsername()))
        || (!userRepository.existsByUsername(signUpRequest.getUsername()) ||
        userRepository.existsByEmail(signUpRequest.getEmail()))) {

      User user = userRepository.findByEmail(signUpRequest.getEmail());
      ConfirmationToken confirmationToken = confirmationTokenRepository.findByUser(user);
      // User is enabled
      if (user.getEnabled()) {
        throw new IllegalStateException("Account was exists!");
      } else {
        if (LocalDateTime.now().isBefore(confirmationToken.getExpressAt()))
          throw new IllegalStateException("Please , confirm link registration send to your email!");
        else
          confirmationTokenRepository.deleteById(confirmationToken.getId());
           userRepository.deleteById(user.getId());

        User userRegistered = new User(
            signUpRequest.getFullName(),
            signUpRequest.getPhone(),
            signUpRequest.getEmail(),
            signUpRequest.getUsername(),
            passwordEncoder.encode(signUpRequest.getPassword())
        );
        Set<Role> roles = new HashSet<>();
        Role role = new Role(1, EnumRole.ROLE_USER);
        roles.add(role);
        userRegistered.setRoles(roles);
        userRepository.save(userRegistered);
        String token = registrationToken(userRegistered);
        String link = "http://localhost:8080/api/auth/confirm?token=" + token;
        emailService.send(signUpRequest.getEmail(), buildEmail(signUpRequest.getFullName(), link));
        return token;
      }
    } else {
      User newUser = new User(
          signUpRequest.getFullName(),
          signUpRequest.getPhone(),
          signUpRequest.getEmail(),
          signUpRequest.getUsername(),
          passwordEncoder.encode(signUpRequest.getPassword())
      );
      Set<Role> roles = new HashSet<>();
      Role role = new Role(1, EnumRole.ROLE_USER);
      roles.add(role);
      newUser.setRoles(roles);
      userRepository.save(newUser);
      String token = registrationToken(newUser);
      String link = "http://localhost:8080/api/auth/confirm?token=" + token;
      emailService.send(signUpRequest.getEmail(), buildEmail(signUpRequest.getFullName(), link));
      return token;
    }
  }

  //Forgot password
  public void forgotPassword(Long id) {

    User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User with id not found"));
    if (!ObjectUtils.isEmpty(user)) {
      String newPassword = String.valueOf(randomPassword.randomPassword(10));
      emailService.sendForgotPassword(user.getEmail(), newPassword);
      user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
      userRepository.save(user);
    }
  }

  //Register user return link confirmed
  @Transactional
  public String registrationToken(User user) {
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(
        token, LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(15), user
    );
    confirmationTokenService.saveConfirmationToken(confirmationToken);
    return token;
  }

  public void enableUser(String email) {
    userRepository.enableUser(email);
  }

  //Confirm token
  @Transactional
  public String confirmToken(String token) {
    ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
        .orElseThrow(() -> new IllegalStateException("Token not found"));

    if (confirmationToken.getConfirmedAt() != null) {
      throw new IllegalStateException("Email already confirmed");
      //TODO method calling at wrong time
    } else {
      LocalDateTime expressAt = confirmationToken.getExpressAt();
      if (expressAt.isBefore(LocalDateTime.now())) {
        throw new IllegalStateException("Token expired");
      }
      confirmationToken.setConfirmedAt(LocalDateTime.now());
      enableUser(confirmationToken.getUser().getEmail());
      return "Confirmed account successful";
    }
  }

  //Build email
  private String buildEmail(String name, String link) {
    return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
        "\n" +
        "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
        "\n" +
        "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;" +
        "width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
        "    <tbody><tr>\n" +
        "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
        "        \n" +
        "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" " +
        "cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
        "          <tbody><tr>\n" +
        "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
        "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" " +
        "style=\"border-collapse:collapse\">\n" +
        "                  <tbody><tr>\n" +
        "                    <td style=\"padding-left:10px\">\n" +
        "                  \n" +
        "                    </td>\n" +
        "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;" +
        "text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
        "                    </td>\n" +
        "                  </tr>\n" +
        "                </tbody></table>\n" +
        "              </a>\n" +
        "            </td>\n" +
        "          </tr>\n" +
        "        </tbody></table>\n" +
        "        \n" +
        "      </td>\n" +
        "    </tr>\n" +
        "  </tbody></table>\n" +
        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" " +
        "cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" " +
        "width=\"100%\">\n" +
        "    <tbody><tr>\n" +
        "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
        "      <td>\n" +
        "        \n" +
        "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"" +
        " style=\"border-collapse:collapse\">\n" +
        "                  <tbody><tr>\n" +
        "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
        "                  </tr>\n" +
        "                </tbody></table>\n" +
        "        \n" +
        "      </td>\n" +
        "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
        "    </tr>\n" +
        "  </tbody></table>\n" +
        "\n" +
        "\n" +
        "\n" +
        "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" " +
        "cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" " +
        "width=\"100%\">\n" +
        "    <tbody><tr>\n" +
        "      <td height=\"30\"><br></td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
        "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;" +
        "max-width:560px\">\n" +
        "        \n" +
        "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p" +
        "><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. " +
        "Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;" +
        "border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p " +
        "style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate" +
        " Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
        "        \n" +
        "      </td>\n" +
        "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
        "    </tr>\n" +
        "    <tr>\n" +
        "      <td height=\"30\"><br></td>\n" +
        "    </tr>\n" +
        "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
        "\n" +
        "</div></div>";
  }
}
