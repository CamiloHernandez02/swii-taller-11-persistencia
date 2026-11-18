package com.rica.polyglot.persistencia;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * CacheRedis: Capa de caché para lecturas frecuentes
 * TTL: 1 hora
 */
@Service
public class CacheRedis {
    private static final String KEY_PREFIX = "pub:";
    private static final long TTL_MINUTES = 60;
    
    private final RedisTemplate<String, String> redisTemplate;

    public CacheRedis(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(KEY_PREFIX + key, value, TTL_MINUTES, TimeUnit.MINUTES);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + key);
    }

    public void delete(String key) {
        redisTemplate.delete(KEY_PREFIX + key);
    }

    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
