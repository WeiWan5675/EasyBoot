package com.weiwan.easyboot.web.interceptor;

import com.weiwan.easyboot.config.BootDataAuthorityHandler;
import com.weiwan.easyboot.config.BootMyBatisPlusConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataAuthorityClearInterceptor implements HandlerInterceptor {

    @Autowired
    private BootDataAuthorityHandler bootDataAuthorityHandler;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        bootDataAuthorityHandler.clearThreadLocal();
    }
}