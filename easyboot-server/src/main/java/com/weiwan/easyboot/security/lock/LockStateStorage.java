package com.weiwan.easyboot.security.lock;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 17:35
 * @Package: com.weiwan.easyboot.security.lock
 * @ClassName: LockStateStorage
 * @Description: 锁定状态存储
 **/
public interface LockStateStorage {

    boolean existKey(String key);

    void setKey(String key, Integer count);

    Integer getKey(String key);

    void deleteKey(String key);

    void expired(String key, Duration expireTime);

    void clearAll();
}
