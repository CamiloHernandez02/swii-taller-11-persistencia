package com.rica.publicaciones.persistencia;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheRedis {
    private static final Logger logger = LoggerFactory.getLogger(CacheRedis.class);
    private static final String CACHE_PREFIX = "pub:";
    private static final long TTL_HORAS = 1;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void guardar(PublicacionSql pub) {
        try {
            String key = CACHE_PREFIX + pub.getId();
            String value = objectMapper.writeValueAsString(pub);
            redisTemplate.opsForValue().set(key, value, TTL_HORAS, TimeUnit.HOURS);
            logger.debug("Publicación guardada en Redis: {}", pub.getId());
        } catch (Exception e) {
            logger.error("Error guardando en Redis", e);
        }
    }

    public PublicacionSql obtener(String id) {
        try {
            String key = CACHE_PREFIX + id;
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                logger.debug("Publicación obtenida de Redis: {}", id);
                return objectMapper.readValue(value, PublicacionSql.class);
            }
        } catch (Exception e) {
            logger.error("Error leyendo de Redis", e);
        }
        return null;
    }

    public void eliminar(String id) {
        try {
            String key = CACHE_PREFIX + id;
            redisTemplate.delete(key);
            logger.debug("Publicación eliminada de Redis: {}", id);
        } catch (Exception e) {
            logger.error("Error eliminando de Redis", e);
        }
    }
}
