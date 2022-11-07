package com.practice.overviewspringsecurity.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "username")
})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "Full name is not null")
  @Size(max = 50)
  private String fullName;

  @NotBlank(message = "File data is valid")
  @Size(min = 10)
  private String phone;

  @NotBlank
  private String email;

  @NotBlank
  private String username;

  private String password;

  private Boolean locked = false;

  private Boolean enabled = false;


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  public User(String fullName, String phone, String email, String username, String password) {
    this.fullName = fullName;
    this.phone = phone;
    this.email = email;
    this.username = username;
    this.password = password;
  }
}
