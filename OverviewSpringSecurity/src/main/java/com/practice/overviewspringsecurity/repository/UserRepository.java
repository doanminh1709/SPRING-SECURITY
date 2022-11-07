package com.practice.overviewspringsecurity.repository;

import com.practice.overviewspringsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);

  User findByEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE User b SET b.enabled = TRUE WHERE b.email = ?1")
  void enableUser(@Param("email") String email);

  @Query("SELECT a.enabled FROM User a WHERE a.id =:id")
  Boolean checkUserEnabled(@Param("id") Long id);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
