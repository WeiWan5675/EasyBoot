package com.weiwan.easyboot.component.cache;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 17:59
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: LocalCacheServiceImpl
 * @Description: 本地缓存实现
 **/
@Service
public class LocalCacheServiceImpl implements LocalCacheService, InitializingBean {

    @Autowired
    private LocalCacheManager localCacheManager;

    private LocalCacheStorage<String, Object> localCacheStorage;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.localCacheStorage = localCacheManager.getCacheStorage();
    }


    @Override
    public Object get(String k) {
        return localCacheStorage.get(k);
    }

    @Override
    public Object getKeyOrDefault(String k, Object var) {
        return localCacheStorage.getOrDefault(k, var);
    }

    @Override
    public void set(String k, Object v) {
        localCacheStorage.set(k, v);
    }

    @Override
    public void set(String k, Object v, Duration duration) {
        localCacheStorage.set(k, v, duration);
    }

    @Override
    public void expired(String k, Duration duration) {
        localCacheStorage.expired(k, duration);
    }

    @Override
    public void remove(String k) {
        localCacheStorage.remove(k);
    }

    @Override
    public Object getRemove(String k) {
        return localCacheStorage.getRemove(k);
    }

    @Override
    public void clearAll() {
        localCacheStorage.clearAll();
    }

    @Override
    public LocalCacheManager getCacheManager() {
        return localCacheManager;
    }

}
