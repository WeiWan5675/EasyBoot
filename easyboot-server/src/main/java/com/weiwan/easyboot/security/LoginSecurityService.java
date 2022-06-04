package com.weiwan.easyboot.security;

import com.weiwan.easyboot.component.cache.LocalCacheService;
import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.model.enums.LockStorageType;
import com.weiwan.easyboot.security.lock.*;
import com.weiwan.easyboot.service.AbstractBaseService;
import com.weiwan.easyboot.component.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xiaozhennan
 */
@Service
public class LoginSecurityService extends AbstractBaseService implements InitializingBean {

    @Autowired
    private BootProperties bootProperties;

    @Autowired
    private LocalCacheService localCacheService;

    private BootProperties.LoginProperties loginProperties;
    private LockStrategy usernameLockStrategy;
    private LockStrategy ipLockStrategy;
    private LockStateStorage lockStateStorage;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.loginProperties = bootProperties.getLogin();
        LockStorageType lockStorage = loginProperties.getLockStorage();
        if (lockStorage == LockStorageType.REDIS) {
            RedisTemplate<String, Object> redisTemplate = SpringContextHolder.getBean("redisTemplate", RedisTemplate.class);
            lockStateStorage = new RedisLockStateStorage(redisTemplate);
        } else {
            lockStateStorage = new CacheLockStateStorage(localCacheService.getCacheManager().getCacheStorage());
        }

        usernameLockStrategy = new UsernameLockStrategy(loginProperties.getLockTime(), loginProperties.getLockPasswdFailTimes(), lockStateStorage);
        ipLockStrategy = new IpLockStrategy(loginProperties.getLockTime(), loginProperties.getLockIpFailTimes(), lockStateStorage);
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
