package com.weiwan.easyboot.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.easyboot.event.BootEventBus;
import com.weiwan.easyboot.model.Result;
import com.weiwan.easyboot.model.constant.Constants;
import com.weiwan.easyboot.model.constant.LoginResult;
import com.weiwan.easyboot.security.SecurityUtils;
import com.weiwan.easyboot.security.LoginUserDetails;
import com.weiwan.easyboot.security.jwt.JwtTokenProvider;
import com.weiwan.easyboot.security.listener.LoginEventListener;
import com.weiwan.easyboot.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 前后端分离场景下的成功处理
 *
 * @author xiaozhennan
 */
public class LoginSuccessHandler extends AbstractJsonResponeHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = authentication.getName();
        LoginEventListener.LoginResultMsg loginResultMsg = new LoginEventListener.LoginResultMsg(username, new Date(),
                IpUtil.getRemoteIp(request), request.getHeader("User-Agent"));
        loginResultMsg.setResult(LoginResult.SUCCESS);
        loginResultMsg.setUserId(SecurityUtils.getUserId());
        BootEventBus.publish(loginResultMsg);

        // 签发token
        LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
        String token = authenticationService.createToken(userDetails);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userDetails.getUserInfo().getUserId());
        jsonObject.put("token", token);
        response.setHeader(Constants.TOKEN_KEY, token);
        super.response(response, Result.ok(jsonObject));
    }
}
