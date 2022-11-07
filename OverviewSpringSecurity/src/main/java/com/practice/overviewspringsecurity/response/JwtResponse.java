package com.practice.overviewspringsecurity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

  private Long id;

  private String fullName;

  private String email;

  private String phone;

  private String username;

  private List<String> roles;

  private String accessToken;

  private String refreshToken;

  private final String type = "Bearer";

  }

