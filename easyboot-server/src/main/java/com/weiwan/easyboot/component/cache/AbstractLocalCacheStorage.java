package com.weiwan.easyboot.component.cache;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/6/3 14:49
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: AbstractLocalCacheStorage
 * @Description: 本地缓存抽象类
 **/
public abstract class AbstractLocalCacheStorage<K, V, C> implements LocalCacheStorage<K, V> {

    protected C localCache;

    @Override
    public void init() throws Exception {
        //什么都不需要做
    }

    @Override
    public V getOrDefault(K k, V var) {
        V value = this.get(k);
        if (value == null) {
            return var;
        }
        return value;
    }

    @Override
    public void set(K s, V o, Duration duration) {
        this.set(s, o);
        this.expired(s, duration);
    }

    @Override
    public V getRemove(K k) {
        V v = this.get(k);
        this.remove(k);
        return v;
    }

    @Override
    public boolean exist(K k) {
        return this.get(k) != null;
    }

    @Override
    public void close() {
        // 什么都不需要做
    }

}
