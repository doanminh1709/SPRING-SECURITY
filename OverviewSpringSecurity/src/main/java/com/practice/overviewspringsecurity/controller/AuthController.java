package com.practice.overviewspringsecurity.controller;

import com.practice.overviewspringsecurity.entity.RefreshToken;
import com.practice.overviewspringsecurity.exception.TokenRefreshException;
import com.practice.overviewspringsecurity.repository.UserRepository;
import com.practice.overviewspringsecurity.request.LoginRequest;
import com.practice.overviewspringsecurity.request.SignUpRequest;
import com.practice.overviewspringsecurity.request.TokenRefreshRequest;
import com.practice.overviewspringsecurity.response.JwtResponse;
import com.practice.overviewspringsecurity.response.MessageResponse;
import com.practice.overviewspringsecurity.response.TokenRefreshResponse;
import com.practice.overviewspringsecurity.security.jwt.JwtUtils;
import com.practice.overviewspringsecurity.service.RefreshTokenService;
import com.practice.overviewspringsecurity.service.RegistrationService;
import com.practice.overviewspringsecurity.service.UserDetailImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private RegistrationService registrationService;

  @Autowired
  private AuthenticationManager authenticationManager;


  @PostMapping("/signUp")
  public ResponseEntity<?> registerAccount(@Valid @RequestBody SignUpRequest signUpRequest) {
    String registrationToken = registrationService.register(signUpRequest);
//    return ResponseEntity.status(HttpStatus.OK).body("Register account successful" + "\nRegistration token : " +
//    registrationToken);
    return new ResponseEntity<>(registrationToken, HttpStatus.CREATED);
  }

  @PostMapping("/signIn")
  public ResponseEntity<?> signUpUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(), loginRequest.getPassword()
    ));
    //Load to context holder
    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailImp userDetails = (UserDetailImp) authentication.getPrincipal();
    if (!userRepository.checkUserEnabled(userDetails.getId())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
          body(new MessageResponse("This account is not unauthorized!"));
    }
    String jwt = jwtUtils.generateTokenFromUserName(userDetails.getUsername());

    List<String> roles = userDetails.getAuthorities().stream().
        map(GrantedAuthority::getAuthority).collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(
        userDetails.getId(),
        userDetails.getFullName(),
        userDetails.getEmail(),
        userDetails.getPhone(),
        userDetails.getUsername(),
        roles, jwt, refreshToken.getToken()));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
    String refreshToken = request.getRefreshToken();
    return refreshTokenService.findByToken(refreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUserName(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, refreshToken));
        }).orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));

  }

  @PostMapping("/signOut")
  public ResponseEntity<?> logoutUser() {
    UserDetailImp userDetail = (UserDetailImp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    refreshTokenService.deleteByUser(userDetail.getId());
    return ResponseEntity.status(HttpStatus.OK).body("SignOut successful!");
  }

  @GetMapping("/confirm")
  public String confirmedToken(@RequestParam("token") String token) {
    return registrationService.confirmToken(token);
  }

  @GetMapping("/forgotPassword/{id}")
  public ResponseEntity<?> forgotPassword(@PathVariable("id") Long id) {
    registrationService.forgotPassword(id);
    return ResponseEntity.ok(new MessageResponse(" Change password successfully!"));
  }
}
