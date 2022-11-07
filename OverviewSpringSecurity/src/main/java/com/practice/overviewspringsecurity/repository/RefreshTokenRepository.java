package com.practice.overviewspringsecurity.repository;

import com.practice.overviewspringsecurity.entity.RefreshToken;
import com.practice.overviewspringsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);

}
