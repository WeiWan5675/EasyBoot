package com.weiwan.easyboot.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.easyboot.model.constant.Constants;
import com.weiwan.easyboot.security.LoginUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, HttpStatusEntryPoint authenticationEntryPoint, UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager, authenticationEntryPoint);
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(Constants.TOKEN_KEY);
        Authentication authentication = null;
        try {
            //token判空
            if (StringUtils.isBlank(token)) {
                SecurityContextHolder.clearContext();
                logger.info("无token");
                filterChain.doFilter(request, response);
                return;
            } else {
                token = token.substring(1, token.length()-1);
            }
            String username = jwtTokenProvider.getSubject(token);
            Date expiration = jwtTokenProvider.getExpiration(token);
            Date currentDate = new Date();
            if (expiration.before(currentDate)) {
                logger.info("expired token, username: {}", username);
                filterChain.doFilter(request, response);
                return;
            }

            //校验token时效是否不足, 同时创建Authentication
            if (expiration.getTime() - currentDate.getTime() <= Constants.TOKEN_EXPIRATION_TIME / 2) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                token = jwtTokenProvider.createToken((LoginUserDetails) userDetails);
                response.setHeader(Constants.TOKEN_KEY, token);
            } else {
                authentication = getAuthentication(token);
            }

        } catch (Exception e) {
            logger.error("Invalid jwt: {}", e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }


    public Authentication getAuthentication(String token) throws JsonProcessingException {
        Authentication authentication = jwtTokenProvider.createAuthentication(token);
        return authentication;
    }
}