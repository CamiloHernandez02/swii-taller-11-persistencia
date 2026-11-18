package com.rica.publicaciones.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheRedisTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    private CacheRedis cache;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        cache = new CacheRedis(redisTemplate, objectMapper);
    }

    @Test
    void testGuardarEnCache() throws Exception {
        PublicacionSql pub = new PublicacionSql("pub-001", "inv-001", "Título", "10.1234/test", 2024, null);

        cache.guardar(pub);

        verify(valueOps, times(1)).set(anyString(), anyString(), eq(1L), any());
    }

    @Test
    void testObtenerDelCache() throws Exception {
        PublicacionSql pub = new PublicacionSql("pub-001", "inv-001", "Título", "10.1234/test", 2024, null);
        String json = objectMapper.writeValueAsString(pub);
        when(valueOps.get("pub:pub-001")).thenReturn(json);

        PublicacionSql result = cache.obtener("pub-001");

        assertNotNull(result);
        assertEquals("pub-001", result.getId());
    }
}
