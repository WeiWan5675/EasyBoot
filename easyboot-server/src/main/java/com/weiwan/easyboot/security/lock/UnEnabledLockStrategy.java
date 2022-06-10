package com.weiwan.easyboot.security.lock;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/6/5 19:08
 * @Package: com.weiwan.easyboot.security.lock
 * @ClassName: UnEnabledLockStrategy
 * @Description: 未启用用户锁定逻辑实现
 **/
public class UnEnabledLockStrategy extends AbstractLockStrategy{

    public UnEnabledLockStrategy(Duration lockTime, int lockFailTimes, LockStateStorage lockStateStorage) {
        super(lockTime, lockFailTimes, lockStateStorage);
    }

    public UnEnabledLockStrategy() {
    }

    @Override
    public long increment(String key) {
        return 1;
    }

    @Override
    public boolean isLocked(String key) {
        return false;
    }

    @Override
    public void clear(String key) {

    }

    @Override
    protected String wrapKey(String key) {
        return null;
    }
}
