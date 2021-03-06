package com.weiwan.easyboot.security;

import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.security.handler.AdminAccessDeniedHandler;
import com.weiwan.easyboot.security.handler.LoginFailureHandler;
import com.weiwan.easyboot.security.handler.LoginSuccessHandler;
import com.weiwan.easyboot.security.handler.LogoutSuccessHandler;
import com.weiwan.easyboot.security.jwt.JwtAuthenticationFilter;
import com.weiwan.easyboot.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * @Author: xiaozhennan
 * @Date: 2022/6/4 14:39
 * @Package: com.weiwan.easyboot.security
 * @ClassName: SecurityConfig
 * @Description: ??????SpringSecurity??????
 **/
@Configuration
@EnableWebSecurity
@ControllerAdvice
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {
    public static final String[] STATIC_RESOURCES =
            {"/**/*.html", "/**/*.js", "/**/*.css", "/**/*.ico", "/**/*.png", "/**/*.jpg"};
    // public static final String[] DOC_API = {"/swagger-resources/**", "/**/api-docs"};
    private static final String DEFAULT_REMEMBER_ME_NAME = "rememberMe";
    private static final String PUBLIC_ANT_PATH = "/public/**";
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_OUT_URL = "/logout";

    private final BootProperties bootProperties;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // @formatter:off
        // ????????????????????????????????????
        http.authorizeRequests()
                // ??????/public/**
                .antMatchers(PUBLIC_ANT_PATH).permitAll()
                // ????????????,????????????
                .anyRequest().authenticated();
        // ??????username filter provider ????????????
        http.formLogin().loginProcessingUrl(LOGIN_URL).successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler());

        // ????????????????????? ???????????????????????????????????????????????????UsernamePasswordAuthenticationFilter-> DaoAuthenticationProvider ??????????????????
        // ????????????
        http.logout().logoutUrl(LOGIN_OUT_URL).logoutSuccessHandler(logoutSuccessHandler());
        // ???????????????????????????????????????accessDeniedHandler ?????????????????????403???spring security
        // ??????filter????????????????????????handler???,?????????????????????????????????@ExceptionHandler ??????
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                // ??????????????????????????????,???????????????
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // remember ????????????????????????remember-cookie
        http.rememberMe().rememberMeParameter(DEFAULT_REMEMBER_ME_NAME)
                .key(bootProperties.getLogin().getRememberKey()).useSecureCookie(true)
                .tokenValiditySeconds((int) bootProperties.getLogin().getRememberTime().getSeconds())
                .userDetailsService(userDetailsService);

        // ??????????????????spring boot ?????????????????????
        http.cors();
        // ???????????????
        http.headers().defaultsDisabled()// ????????????
                // ???????????????respone content type ??????????????????
                .contentTypeOptions()
                // xss ??????????????????????????????????????????????????????????????????????????????
                .and().xssProtection()
                // ?????????????????????frame
                .and().frameOptions().sameOrigin()
                // CSRF ??????
                // respone cookie name XSRF-TOKEN
                // requst param _csrf or below;
                // request head HEADER_NAME = "X-CSRF-TOKEN";
                // CsrfFilter ????????????????????????????????????get??????
                .and().csrf().ignoringAntMatchers(PUBLIC_ANT_PATH, LOGIN_URL)
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());// .disable();

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), userDetailsService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ??????????????????
        return (web) -> web.ignoring().antMatchers(STATIC_RESOURCES);
    }

    /**
     * ?????????session ????????????????????????
     *
     * @return
     * @see HttpSecurity#sessionManagement()
     */
    @Bean
    @ConditionalOnProperty(name = "easyboot.login.concurrent-session-control-enabled", havingValue = "true")
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }




    /**
     * ??????AuthenticationManager?????????????????????????????????????????????
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }




    /**
     * ??????????????????@ExceptionHandler ?????????????????????????????????????????????accessDeniedHandler
     *
     * @param e
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException e) {
        throw e;
    }



    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AdminAccessDeniedHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordUtil.getEncoder();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        return daoAuthenticationProvider;
    }




}
