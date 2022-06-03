package com.weiwan.easyboot.security.lock;

import java.time.Duration;

import org.springframework.data.redis.core.ValueOperations;

/**
 * @author xiaozhennan
 */
public class IpLockStrategy extends AbstractLockStrategy {

    private static final String PREFIX = "login_err_cnt_ip_";

    public IpLockStrategy(Duration lockTime, int lockFailTimes, LockStateStorage lockStateStorage) {
        super(lockTime, lockFailTimes, lockStateStorage);
    }

    @Override
    protected String wrapKey(String key) {
        return PREFIX + key;
    }
}
