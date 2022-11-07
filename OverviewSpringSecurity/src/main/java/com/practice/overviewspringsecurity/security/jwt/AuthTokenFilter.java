package com.practice.overviewspringsecurity.security.jwt;

import com.practice.overviewspringsecurity.service.UserDetailServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class AuthTokenFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailServiceImp userDetailService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    String jwtToken = parseJwt(request);
    if (jwtToken != null && jwtUtils.validateToken(jwtToken)) {

      //1.get username
      String username = jwtUtils.getUserNameFromToken(jwtToken);

      //2.get user detail
      UserDetails userDetails = userDetailService.loadUserByUsername(username);

      //3.authentication
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      //4.set authentication
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      //5.Load authentication to context holder
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request , response);
  }

  public String parseJwt(HttpServletRequest request) {

    String headerAuth = request.getHeader(AUTHORIZATION);
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      return headerAuth.substring(7);
    }
    return null;
  }

}
