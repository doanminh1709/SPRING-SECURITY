package com.practice.overviewspringsecurity.controller;

import com.practice.overviewspringsecurity.entity.User;
import com.practice.overviewspringsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @GetMapping("/findAll")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> getAllUser() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  @GetMapping("/get/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getById(@PathVariable("id") Long id) {
    User user = userRepository.findById(id).
        orElseThrow(() -> new UsernameNotFoundException("Not found user with id"));
    return ResponseEntity.ok(user);
  }
}
