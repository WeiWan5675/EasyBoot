package com.weiwan.easyboot.security.lock;

import com.weiwan.easyboot.component.cache.LocalCacheStorage;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 17:42
 * @Package: com.weiwan.easyboot.security.lock
 * @ClassName: CacheLockStateStorage
 * @Description: 基于LoadingCache的锁定状态存储
 **/
public class CacheLockStateStorage implements LockStateStorage {

    private LocalCacheStorage<String, Object> localCacheStorage;


    public CacheLockStateStorage(LocalCacheStorage<String, Object> cacheStorage) {
        this.localCacheStorage = cacheStorage;
    }

    @Override
    public boolean existKey(String key) {
        return this.localCacheStorage.exist(key);
    }

    @Override
    public void setKey(String key, Integer count) {
        localCacheStorage.set(key, count);
    }


    @Override
    public Integer getKey(String key) {
        return (Integer) localCacheStorage.get(key);
    }

    @Override
    public void deleteKey(String key) {
        this.localCacheStorage.remove(key);
    }

    @Override
    public void expired(String key, Duration expireTime) {
        this.localCacheStorage.expired(key, expireTime);
    }


    @Override
    public void clearAll() {

    }
}
