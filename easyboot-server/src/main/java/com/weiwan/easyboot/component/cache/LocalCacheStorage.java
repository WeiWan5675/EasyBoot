package com.weiwan.easyboot.component.cache;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/6/3 14:47
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: LocalCacheStorage
 * @Description: 本地缓存接口
 **/
public interface LocalCacheStorage<K, V> {

    void init() throws Exception;

    V get(K k);

    V getOrDefault(K k, V var);

    void set(K k, V v);

    boolean exist(K k);

    void set(K k, V v, Duration duration);

    void expired(K k, Duration duration);

    void remove(K k);

    V getRemove(K k);

    void clearAll();

    void close();

}
