package com.practice.overviewspringsecurity.service;

import com.practice.overviewspringsecurity.entity.ConfirmationToken;
import com.practice.overviewspringsecurity.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationToken;

  //save confirmation token
  public void saveConfirmationToken(ConfirmationToken token){
    confirmationToken.save(token);
  }
  //get token
  public Optional<ConfirmationToken> getToken(String token){
    return confirmationToken.findByToken(token);
  }

  //setConfirmedAt
  public int setConfirmedAt(String token){
    return confirmationToken.updateConfirmedAt(token , LocalDateTime.now());
  }
}
