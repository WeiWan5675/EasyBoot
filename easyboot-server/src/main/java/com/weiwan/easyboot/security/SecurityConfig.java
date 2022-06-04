package com.weiwan.easyboot.security;

import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.security.handler.AdminAccessDeniedHandler;
import com.weiwan.easyboot.security.handler.AjaxAuthenticationFailureHandler;
import com.weiwan.easyboot.security.handler.AjaxAuthenticationSuccessHandler;
import com.weiwan.easyboot.security.handler.AjaxLogoutSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author: xiaozhennan
 * @Date: 2022/6/4 14:39
 * @Package: com.weiwan.easyboot.security
 * @ClassName: SecurityConfig
 * @Description: 新版SpringSecurity配置
 **/
@Configuration
@EnableWebSecurity
@ControllerAdvice
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String[] STATIC_RESOURCES =
            {"/**/*.html", "/**/*.js", "/**/*.css", "/**/*.ico", "/**/*.png", "/**/*.jpg"};
    // public static final String[] DOC_API = {"/swagger-resources/**", "/**/api-docs"};
    private static final String DEFAULT_REMEMBER_ME_NAME = "rememberMe";
    private static final String PUBLIC_ANT_PATH = "/public/**";
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_OUT_URL = "/logout";

    private final BootProperties bootProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        // 需要认证和授权的请求配置
        http.authorizeRequests()
                // 放行/public/**
                .antMatchers(PUBLIC_ANT_PATH).permitAll()
                // 放行static资源
                .antMatchers(STATIC_RESOURCES).permitAll()
                // 所有请求,需要验证
                .anyRequest().authenticated();
        // 自带username filter provider 处理机制
        http.formLogin().loginProcessingUrl(LOGIN_URL).successHandler(ajaxAuthenticationSuccessHandler())
                .failureHandler(ajaxAuthenticationFailureHandler());

        // 以下为公共逻辑 如果要扩展登录方式，只需要添加类似UsernamePasswordAuthenticationFilter-> DaoAuthenticationProvider 这种整套逻辑
        // 登出处理
        http.logout().logoutUrl(LOGIN_OUT_URL).logoutSuccessHandler(ajaxLogoutSuccessHandler());
        // 默认认证过程中的异常处理，accessDeniedHandler 默认为也是返回403，spring security
        // 是在filter级别抓住异常回调handler的,所以会被全局拦截器模式@ExceptionHandler 吃掉
        http.exceptionHandling().accessDeniedHandler(new AdminAccessDeniedHandler())
                // 到认证环节的入口逻辑,默认是跳页
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        if (bootProperties.getLogin().isConcurrentSessionControlEnabled()) {
            // seesion 管理 一个账号登录一次，maxSessionsPreventsLogin=false后面的挤掉前面的,true直接报错,需要添加下方httpSessionEventPublisher
            http.sessionManagement().maximumSessions(bootProperties.getLogin().getMaximumSessions())
                    .maxSessionsPreventsLogin(bootProperties.getLogin().isMaxSessionsPreventsLogin())
                    // 不使用spring session 不需要指定，默认是内存逻辑
                    .sessionRegistry(new SessionRegistryImpl(new ConcurrentHashMap<>(), new ConcurrentHashMap<>()));
        }

        // remember 采用默认解密前端remember-cookie
        http.rememberMe().rememberMeParameter(DEFAULT_REMEMBER_ME_NAME)
                .key(bootProperties.getLogin().getRememberKey()).useSecureCookie(true)
                .tokenValiditySeconds((int) bootProperties.getLogin().getRememberTime().getSeconds())
                .userDetailsService(adminUserDetailsService());

        // 需要添加不然spring boot 的跨域配置无效
        http.cors();
        // 安全头设置
        http.headers().defaultsDisabled()// 关闭默认
                // 浏览器根据respone content type 格式解析资源
                .contentTypeOptions()
                // xss 攻击，限制有限，还是需要通过过滤请求参数，该框架已做
                .and().xssProtection()
                // 同域名可以通过frame
                .and().frameOptions().sameOrigin()
                // CSRF 攻击
                // respone cookie name XSRF-TOKEN
                // requst param _csrf or below;
                // request head HEADER_NAME = "X-CSRF-TOKEN";
                // CsrfFilter 默认实现类是这个，不拦截get请求
                .and().csrf().ignoringAntMatchers(PUBLIC_ANT_PATH, LOGIN_URL)
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());// .disable();

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(AdminPasswordEncoder.getEncoder());
        daoAuthenticationProvider.setUserDetailsService(adminUserDetailsService());
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        http.authenticationProvider(daoAuthenticationProvider);


        // @formatter:on
        return http.build();
    }


    /**
     * 当开启session 并发控制时候需要
     *
     * @return
     * @see HttpSecurity#sessionManagement()
     */
    @Bean
    @ConditionalOnProperty(name = "easyboot.login.concurrent-session-control-enabled", havingValue = "true")
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 防止被上层的@ExceptionHandler 范围更大的给拦截住，而无法调用accessDeniedHandler
     * @param e
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException e) {
        throw e;
    }


    @Bean
    public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler() {
        return new AjaxAuthenticationSuccessHandler();
    }

    @Bean
    public AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler() {
        return new AjaxLogoutSuccessHandler();
    }

    @Bean
    public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler() {
        return new AjaxAuthenticationFailureHandler();
    }

    @Bean
    public AdminUserDetailsServiceImpl adminUserDetailsService() {
        return new AdminUserDetailsServiceImpl();
    }


}
