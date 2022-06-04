package com.weiwan.easyboot.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.weiwan.easyboot.model.Result;

/**
 * @author xiaozhennan
 */
public class AjaxLogoutSuccessHandler extends AbstractJsonResponeHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {
        super.sendRespone(response, Result.SUCCESS);
    }
}
