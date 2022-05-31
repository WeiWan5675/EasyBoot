package com.weiwan.easyboot.security.lock;

import java.time.Duration;

import org.springframework.data.redis.core.ValueOperations;

/**
 * @author hdf
 */
public class IpLockStrategy extends AbstractLockStrategy {

    private static final String PREFIX = "login_err_cnt_ip_";

    public IpLockStrategy(Duration lockTime, int lockFailTimes, ValueOperations<String, Integer> valueOperations) {
        super(lockTime, lockFailTimes, valueOperations);
    }

    @Override
    protected String wrapKey(String key) {
        return PREFIX + key;
    }
}
