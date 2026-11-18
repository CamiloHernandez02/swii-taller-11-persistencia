package com.rica.polyglot.persistencia;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CacheRedisTest {
    @Test
    public void testSetAndGet() {
        RedisTemplate<String, String> template = mock(RedisTemplate.class);
        CacheRedis cache = new CacheRedis(template);
        
        cache.set("test-key", "test-value");
        
        verify(template.opsForValue()).set("pub:test-key", "test-value", 60, java.util.concurrent.TimeUnit.MINUTES);
    }

    @Test
    public void testDelete() {
        RedisTemplate<String, String> template = mock(RedisTemplate.class);
        CacheRedis cache = new CacheRedis(template);
        
        cache.delete("test-key");
        
        verify(template).delete("pub:test-key");
    }
}
