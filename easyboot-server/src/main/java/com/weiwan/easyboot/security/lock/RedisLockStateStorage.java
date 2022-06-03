package com.weiwan.easyboot.security.lock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 17:40
 * @Package: com.weiwan.easyboot.security.lock
 * @ClassName: RedisLockStateStorage
 * @Description: Redis作为锁定状态存储
 **/
public class RedisLockStateStorage implements LockStateStorage {
    private final RedisTemplate redisTemplate;
    private final ValueOperations<String, Integer> valueOperations;

    public RedisLockStateStorage(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public boolean existKey(String key) {
        return valueOperations.getOperations().hasKey(key);
    }

    @Override
    public void setKey(String key, Integer count) {
        valueOperations.set(key, count);
    }


    @Override
    public Integer getKey(String key) {
        return valueOperations.get(key);
    }

    @Override
    public void deleteKey(String key) {
        valueOperations.getOperations().delete(key);
    }

    @Override
    public void expired(String key, Duration expireTime) {
        valueOperations.getOperations().expire(key, expireTime);
    }


    @Override
    public void clearAll() {

    }
}
