package com.weiwan.easyboot.component.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 18:07
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: TimedLocalCacheStorage
 * @Description: Hutool缓存工具TimedCache 封装
 **/
public class TimedLocalCacheStorage extends AbstractLocalCacheStorage<String, Object, TimedCache<String, Object>> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    //默认缓存时长 单位s
    private static final long DEFAULT_TIMEOUT = 1000 * 60 * 60 * 8L;
    //默认清理间隔时间 单位s
    private static final Long DEFAULT_CLEANING_INTERVAL = 1 * 60 * 1000L;
    private long timeout;
    private long cleaningInterval;

    public TimedLocalCacheStorage() {
        this.timeout = DEFAULT_TIMEOUT;
        this.cleaningInterval = DEFAULT_CLEANING_INTERVAL;
    }

    public TimedLocalCacheStorage(long defaultTimeout, long cleaningInterval) {
        this.timeout = defaultTimeout;
        this.cleaningInterval = cleaningInterval;
    }

    //缓存对象
    public synchronized void init() {
        // 创建缓存
        this.localCache = CacheUtil.newTimedCache(timeout);
        // 启动定时清理
        this.localCache.schedulePrune(cleaningInterval);
    }

    @Override
    public Object get(String key) {
        return this.localCache.get(key);
    }

    @Override
    public void set(String s, Object o) {
        this.localCache.put(s, o);
    }


    @Override
    public void expired(String s, Duration duration) {
        Object o = this.localCache.get(s);
        if (o != null) {
            this.localCache.put(s, o, duration.toMillis());
        }
    }

    @Override
    public void remove(String s) {
        this.localCache.remove(s);
    }


    @Override
    public void clearAll() {
        this.localCache.clear();
    }

}
