package com.weiwan.easyboot.component.cache;

import cn.hutool.cache.impl.TimedCache;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 17:50
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: LocalCache
 * @Description: 本地缓存
 **/
public interface LocalCacheService {

    Object get(String k);

    Object getKeyOrDefault(String k, Object var);

    void set(String k, Object v);

    void set(String k, Object v, Duration duration);

    void expired(String k, Duration duration);

    void remove(String k);

    Object getRemove(String k);

    void clearAll();

    public LocalCacheManager getCacheManager();

}
