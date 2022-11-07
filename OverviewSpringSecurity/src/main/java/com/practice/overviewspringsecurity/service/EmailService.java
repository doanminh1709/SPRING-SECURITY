package com.practice.overviewspringsecurity.service;

import com.practice.overviewspringsecurity.email.EmailSender;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class EmailService  implements EmailSender {

  private final Logger logger = LoggerFactory.getLogger(EmailService.class);
  private final JavaMailSender javaMailSender;

  @Async
  @Override
  public void send(String to, String email) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
      helper.setSubject("Confirm your email");//Title
      helper.setText(email, true);
      helper.setFrom("doanducminh11082002@gmail.com");
      helper.setTo(to);//To
      javaMailSender.send(message);
    } catch (MessagingException ex) {
      logger.error("Failed to send email", ex);
    }
  }

  @Async
  public void sendForgotPassword(String to , String content){
    try{
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message);

      helper.setSubject("New your password ");
      helper.setText("Password : " + content);
      helper.setFrom("doanducminh11082002@gmail.com");
      helper.setTo(to);
      javaMailSender.send(message);
    }catch (MessagingException exception){
      logger.error("Failed to send mail " , exception);
    }
  }


}
//Async : Trạng thái không đồng bộ , không muốn chặn khách hàng nên sẽ sử dụng 1 hàng đợi để gửi lại email
