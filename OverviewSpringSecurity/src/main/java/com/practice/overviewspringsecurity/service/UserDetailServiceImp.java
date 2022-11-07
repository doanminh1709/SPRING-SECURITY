package com.practice.overviewspringsecurity.service;

import com.practice.overviewspringsecurity.entity.User;
import com.practice.overviewspringsecurity.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@AllArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (ObjectUtils.isEmpty(user)) {
      throw new UsernameNotFoundException("Not found User with username : " + username);
    }
    return UserDetailImp.map(user);
  }
}
