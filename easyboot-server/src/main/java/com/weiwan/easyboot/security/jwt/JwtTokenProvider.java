package com.weiwan.easyboot.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiwan.easyboot.security.LoginUserDetails;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenProvider {
  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  @Value("${easyboot.login.login-secret}")
  private String loginSecret;

  @Value("${easyboot.login.login-expired}")
  private int loginExpired;

  @Autowired
  private ObjectMapper objectMapper;

  public String createToken(LoginUserDetails userDetails) throws JsonProcessingException {
    JwtTokenUtil.setObjectMapper(objectMapper);
    return JwtTokenUtil.generateToken(userDetails, loginSecret, loginExpired, SignatureAlgorithm.HS512);
  }

  public String getSubject(String token) {
    return JwtTokenUtil.getSubject(token, loginSecret);
  }

  public boolean validateJwtToken(String authToken) {
    return JwtTokenUtil.validateToken(authToken, loginSecret);
  }

  public Date getExpiration(String token) {
    return JwtTokenUtil.getExpiration(token, loginSecret);
  }

  public Authentication createAuthentication(String token) throws JsonProcessingException {
    String userDetailsStr = JwtTokenUtil.getUserDetails(token, loginSecret);
    LoginUserDetails loginUserDetails = objectMapper.readValue(userDetailsStr, LoginUserDetails.class);
    Collection<? extends GrantedAuthority> authorities = loginUserDetails.getAuthorities();
    return new UsernamePasswordAuthenticationToken(loginUserDetails, token, authorities);
  }
}
