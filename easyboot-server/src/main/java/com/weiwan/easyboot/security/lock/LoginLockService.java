package com.weiwan.easyboot.security.lock;

import com.weiwan.easyboot.component.SpringContextHolder;
import com.weiwan.easyboot.component.cache.LocalCacheService;
import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.model.enums.LockStorageType;
import com.weiwan.easyboot.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author xiaozhennan
 */
@Service
public class LoginLockService extends AbstractBaseService implements InitializingBean {

    @Autowired
    private BootProperties bootProperties;

    @Autowired
    private LocalCacheService localCacheService;

    private BootProperties.LoginProperties.LoginLockProperties lockProperties;
    private LockStrategy usernameLockStrategy;
    private LockStrategy ipLockStrategy;
    private LockStateStorage lockStateStorage = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.lockProperties = bootProperties.getLogin().getLoginLock();
        if (lockProperties.isEnableUserLock()) {
            LockStateStorage stateStorage = getLockStateStorage();
            usernameLockStrategy = new UsernameLockStrategy(lockProperties.getLockTime(), lockProperties.getLockUserFailTimes(), stateStorage);
        } else {
            usernameLockStrategy = new UnEnabledLockStrategy();
        }
        if (lockProperties.isEnableIpLock()) {
            LockStateStorage stateStorage = getLockStateStorage();
            ipLockStrategy = new IpLockStrategy(lockProperties.getLockTime(), lockProperties.getLockIpFailTimes(), stateStorage);
        }  else {
            ipLockStrategy = new UnEnabledLockStrategy();
        }
    }

    @NotNull
    private LockStateStorage getLockStateStorage() {
        if (lockStateStorage != null) return lockStateStorage;
        synchronized (this) {
            LockStorageType lockStorage = lockProperties.getLockStorage();
            if (lockStorage == LockStorageType.REDIS) {
                RedisTemplate<String, Object> redisTemplate = SpringContextHolder.getBean("redisTemplate", RedisTemplate.class);
                lockStateStorage = new RedisLockStateStorage(redisTemplate);
            } else {
                lockStateStorage = new CacheLockStateStorage(localCacheService.getCacheManager().getCacheStorage());
            }
        }
        return lockStateStorage;
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
