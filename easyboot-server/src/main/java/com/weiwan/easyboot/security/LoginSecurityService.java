package com.weiwan.easyboot.security;

import javax.annotation.Resource;

import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.service.AbstractBaseService;
import com.weiwan.easyboot.security.lock.IpLockStrategy;
import com.weiwan.easyboot.security.lock.LockStrategy;
import com.weiwan.easyboot.security.lock.UsernameLockStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.weiwan.easyboot.config.BootProperties;

import lombok.RequiredArgsConstructor;

/**
 *
 * @author hdf
 */
@Service
@RequiredArgsConstructor
public class LoginSecurityService extends AbstractBaseService implements InitializingBean {

    private final BootProperties bootProperties;
    private BootProperties.LoginProperties loginProperties;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, Integer> valueOperations;
    private LockStrategy usernameLockStrategy;
    private LockStrategy ipLockStrategy;

    @Override
    public void afterPropertiesSet() throws Exception {
        loginProperties = bootProperties.getLogin();
        usernameLockStrategy = new UsernameLockStrategy(loginProperties.getLockTime(),
            loginProperties.getLockPasswdFailTimes(), valueOperations);
        ipLockStrategy =
            new IpLockStrategy(loginProperties.getLockTime(), loginProperties.getLockIpFailTimes(), valueOperations);
    }

    public void clear(String username, String ip) {
        if (StringUtils.isNotBlank(username)) {
            usernameLockStrategy.clear(username);
        }
        if (StringUtils.isNotBlank(ip)) {
            ipLockStrategy.clear(ip);
        }
    }

    public LockStrategy getUsernameLockStrategy() {
        return usernameLockStrategy;
    }

    public LockStrategy getIpLockStrategy() {
        return ipLockStrategy;
    }
}
