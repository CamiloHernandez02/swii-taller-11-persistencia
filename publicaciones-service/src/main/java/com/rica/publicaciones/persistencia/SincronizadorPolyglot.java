package com.rica.publicaciones.persistencia;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SincronizadorPolyglot {
    private static final Logger logger = LoggerFactory.getLogger(SincronizadorPolyglot.class);

    private final PublicacionSqlRepository sqlRepo;
    private final PublicacionMongoRepository mongoRepo;
    private final CacheRedis cache;

    /**
     * Write-through: PostgreSQL es source of truth
     * Propaga automáticamente a MongoDB + Redis
     */
    public PublicacionSql guardar(PublicacionSql pub) {
        logger.info("Guardando publicación en SQL (source of truth)");
        PublicacionSql guardada = sqlRepo.save(pub);

        // Propagar a MongoDB
        PublicacionMongo mongoDoc = new PublicacionMongo(
            pub.getId(),
            pub.getInvestigadorId(),
            pub.getTitulo(),
            pub.getDoi(),
            pub.getAño(),
            System.currentTimeMillis()
        );
        mongoRepo.save(mongoDoc);
        logger.debug("Publicación propagada a MongoDB");

        // Propagar a Redis (cache)
        cache.guardar(pub);
        logger.debug("Publicación guardada en Redis cache");

        return guardada;
    }

    /**
     * GET: Redis → PostgreSQL fallback
     */
    public Optional<PublicacionSql> obtener(String id) {
        // Intentar leer de cache (Redis)
        PublicacionSql cached = cache.obtener(id);
        if (cached != null) {
            logger.debug("Publicación obtenida de Redis cache");
            return Optional.of(cached);
        }

        // Fallback a PostgreSQL
        logger.debug("Cache miss, obteniendo de PostgreSQL");
        Optional<PublicacionSql> result = sqlRepo.findById(id);
        
        // Repopular cache
        result.ifPresent(cache::guardar);
        
        return result;
    }

    /**
     * Eliminar de todos los storages
     */
    public void eliminar(String id) {
        logger.info("Eliminando publicación: {}", id);
        
        sqlRepo.deleteById(id);
        logger.debug("Eliminado de PostgreSQL");
        
        mongoRepo.deleteById(id);
        logger.debug("Eliminado de MongoDB");
        
        cache.eliminar(id);
        logger.debug("Eliminado de Redis");
    }
}
