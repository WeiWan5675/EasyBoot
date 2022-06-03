package com.weiwan.easyboot.security.lock;

import java.time.Duration;

import org.springframework.util.Assert;


/**
 * @author xiaozhennan
 */
public abstract class AbstractLockStrategy implements LockStrategy {

    private final Duration lockTime;
    private final int lockFailTimes;
    private final LockStateStorage lockStateStorage;

    public AbstractLockStrategy(Duration lockTime, int lockFailTimes, LockStateStorage lockStateStorage) {
        this.lockTime = lockTime;
        this.lockFailTimes = lockFailTimes;
        this.lockStateStorage = lockStateStorage;
    }

    @Override
    public long increment(String key) {
        checkKey(key);
        String wrappedKey = wrapKey(key);
        if (!lockStateStorage.existKey(wrappedKey)) {
            lockStateStorage.setKey(wrappedKey, 1);
            lockStateStorage.expired(wrappedKey, lockTime);
            return 1;
        } else {
            Integer oldValue = lockStateStorage.getKey(wrappedKey);
            lockStateStorage.setKey(wrappedKey, oldValue == null ? 1 : oldValue + 1);
            lockStateStorage.expired(wrappedKey, lockTime);
            return lockStateStorage.getKey(wrappedKey);
        }
    }

    private void checkKey(String key) {
        Assert.hasText(key, "key must has text");
    }

    @Override
    public boolean isLocked(String key) {
        checkKey(key);
        String wrappedKey = wrapKey(key);
        Integer failCnt = lockStateStorage.getKey(wrappedKey);
        if (null != failCnt && failCnt >= lockFailTimes) {
            return true;
        }
        return false;
    }

    @Override
    public void clear(String key) {
        checkKey(key);
        String wrappedKey = wrapKey(key);
        lockStateStorage.deleteKey(wrappedKey);
    }

    protected abstract String wrapKey(String key);
}
