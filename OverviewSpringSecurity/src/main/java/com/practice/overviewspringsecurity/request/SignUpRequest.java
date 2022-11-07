package com.practice.overviewspringsecurity.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

  @NotBlank(message = "Full name is not null")
  private String fullName;

  @NotBlank
  @Size(min = 10, message = "Phone must be 10 number")
  private String phone;

  @NotBlank(message = "Email should be a valid email format")
  private String email;

  @NotBlank(message = "Username is not null")
  @Size(max = 50)
  private String username;

  @NotBlank(message = "Password is not null")
  @Size(max = 50)
  private String password;
}
