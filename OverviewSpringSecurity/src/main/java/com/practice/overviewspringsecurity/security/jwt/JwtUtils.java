package com.practice.overviewspringsecurity.security.jwt;

import com.practice.overviewspringsecurity.service.UserDetailImp;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

  private Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwtExpirationMs}")
  private Long jwtExpirationMs;

  //generate token from username
  public String generateTokenFromUserName(String username) {
    return Jwts.builder()
        .setSubject(username)// subject : usesname
        .setIssuedAt(new Date())
        .setExpiration((new Date(new Date().getTime() + jwtExpirationMs)))
        .signWith(SignatureAlgorithm.HS256, secretKey).compact();
  }

  //get username from token
  public String getUserNameFromToken(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  //generate jwt token
  public String generateJwtToken(UserDetailImp userDetailImp) {
    return generateTokenFromUserName(userDetailImp.getUsername());
  }

  //validate token
  public Boolean validateToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }
  //
}
