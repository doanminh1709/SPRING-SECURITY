package com.practice.overviewspringsecurity.service;

import com.practice.overviewspringsecurity.entity.RefreshToken;
import com.practice.overviewspringsecurity.entity.User;
import com.practice.overviewspringsecurity.exception.TokenRefreshException;
import com.practice.overviewspringsecurity.repository.RefreshTokenRepository;
import com.practice.overviewspringsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  private final Long refreshTokenDurationMs = 120000L;

  //find by token
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  //Create refresh token
  public RefreshToken createRefreshToken(Long userId) {

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(userRepository.findById(userId).get());
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

    refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  //Verify token
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired.Pleas signIn again!");
    }
    return token;
  }

  //deleteByUser
  @Transactional
  public void deleteByUser(Long userId) {
    User user = userRepository.findById(userId).
        orElseThrow(() -> new UsernameNotFoundException("User with id not found"));
    refreshTokenRepository.deleteByUser(user);
  }

}
