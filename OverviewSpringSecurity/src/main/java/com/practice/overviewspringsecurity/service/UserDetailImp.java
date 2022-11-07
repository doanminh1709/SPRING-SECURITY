package com.practice.overviewspringsecurity.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.overviewspringsecurity.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailImp implements UserDetails {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String fullName;

  private String email;

  private String phone;

  private String username;

  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public static UserDetailImp map(User user) {
    List<GrantedAuthority> authorities = user.getRoles().
        stream().map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailImp(
        user.getId(),
        user.getFullName(),
        user.getEmail(),
        user.getPhone(),
        user.getUsername(),
        user.getPassword(),
        authorities
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailImp that = (UserDetailImp) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  // là tài khoản chưa hết hạn
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  //Là tài khoản chưa bị khóa
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  // Là thông tin đăng nhập chưa hết hạn
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  //được kích hoạt
  @Override
  public boolean isEnabled() {
    return true;
  }
}
