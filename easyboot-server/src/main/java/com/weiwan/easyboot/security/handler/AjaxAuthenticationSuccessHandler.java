package com.weiwan.easyboot.security.handler;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.weiwan.easyboot.model.constant.LoginResult;
import com.weiwan.easyboot.security.listener.LoginEventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.weiwan.easyboot.event.AdminEventBus;
import com.weiwan.easyboot.security.SecurityUtils;
import com.weiwan.easyboot.common.Result;
import com.weiwan.easyboot.utils.IpUtil;

/**
 * 前后端分离场景下的成功处理
 *
 * @author xiaozhennan
 */
public class AjaxAuthenticationSuccessHandler extends AbstractJsonResponeHandler
    implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        String username = authentication.getName();
        LoginEventListener.LoginResultMsg loginResultMsg = new LoginEventListener.LoginResultMsg(username, new Date(),
            IpUtil.getRemoteIp(request), request.getHeader("User-Agent"));
        loginResultMsg.setResult(LoginResult.SUCCESS);
        loginResultMsg.setUserId(SecurityUtils.getUserId());
        AdminEventBus.publish(loginResultMsg);
        super.sendRespone(response, Result.SUCCESS);
    }

}
