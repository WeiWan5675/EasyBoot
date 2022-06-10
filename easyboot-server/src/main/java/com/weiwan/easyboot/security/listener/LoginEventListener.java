package com.weiwan.easyboot.security.listener;

import com.google.common.eventbus.Subscribe;
import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.event.BootEventBus;
import com.weiwan.easyboot.model.constant.LoginResult;
import com.weiwan.easyboot.model.entity.SysLoginLog;
import com.weiwan.easyboot.security.SecurityUtils;
import com.weiwan.easyboot.security.lock.LoginLockService;
import com.weiwan.easyboot.service.SysLoginLogService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @author xiaozhennan
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginEventListener implements InitializingBean {

    private final SysLoginLogService sysLoginLogService;

    private final LoginLockService loginLockService;
    private final BootProperties bootProperties;

    @Subscribe
    public void listen(LoginResultMsg msg) {
        SysLoginLog sysLoginLog = new SysLoginLog();
        sysLoginLog.setUserId(msg.getUserId());
        sysLoginLog.setUserName(msg.getUserName());
        sysLoginLog.setIp(msg.getIp());
        sysLoginLog.setLoginTime(msg.getLoginTime());
        sysLoginLog.setResult(msg.getResult());
        sysLoginLog.setUserAgent(msg.getUserAgent());
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(msg.getUserAgent());
            Browser browser = userAgent.getBrowser();
            OperatingSystem os = userAgent.getOperatingSystem();
            String deviceName = os.getName() + " " + os.getDeviceType();
            String browserDetail = browser.getName() + " " + browser.getVersion(msg.getUserAgent());
            sysLoginLog.setBrowserName(browserDetail);
            sysLoginLog.setDeviceName(deviceName);
        } catch (Exception e) {
            log.error("parse userAgent error", e);
        }
        if (LoginResult.SUCCESS == msg.getResult()) {
            loginLockService.clear(msg.getUserName(), msg.getIp());
        } else if (LoginResult.PASSWD_ERROR == msg.getResult()) {
            loginLockService.getIpLockStrategy().increment(msg.getIp());
            if (!Objects.equals(msg.getUserId(), SecurityUtils.SUPER_ADMIN_USER_ID)) {
                loginLockService.getUsernameLockStrategy().increment(msg.getUserName());
            }
        } else if (LoginResult.USERNAME_NOT_FOUND == msg.getResult()) {
            loginLockService.getIpLockStrategy().increment(msg.getIp());
        }
        if (bootProperties.getLogin().isRecordLog()) {
            sysLoginLogService.save(sysLoginLog);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        BootEventBus.register(this);
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    public static class LoginResultMsg {

        private final String userName;
        private final Date loginTime;
        private final String ip;
        private final String userAgent;
        private Integer result;
        private Integer userId;
    }
}
