package com.rica.persistence.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class CacheRepository {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public void setOrderCache(String orderId, String orderData, long timeoutSeconds) {
        String key = "order:" + orderId;
        set(key, orderData, timeoutSeconds, TimeUnit.SECONDS);
    }

    public String getOrderCache(String orderId) {
        String key = "order:" + orderId;
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    public void invalidateOrderCache(String orderId) {
        String key = "order:" + orderId;
        delete(key);
    }
}
