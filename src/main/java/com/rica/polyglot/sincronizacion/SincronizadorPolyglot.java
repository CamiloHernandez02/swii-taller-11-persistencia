package com.rica.polyglot.sincronizacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rica.polyglot.persistencia.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SincronizadorPolyglot: Sincroniza datos entre PostgreSQL, MongoDB y Redis
 * Estrategia: Write-through a PostgreSQL, propagate a MongoDB y Redis
 */
@Service
public class SincronizadorPolyglot {
    private static final Logger logger = LoggerFactory.getLogger(SincronizadorPolyglot.class);
    
    private final PublicacionSqlRepository sqlRepo;
    private final PublicacionMongoRepository mongoRepo;
    private final CacheRedis cache;
    private final ObjectMapper mapper;

    public SincronizadorPolyglot(PublicacionSqlRepository sqlRepo,
                                PublicacionMongoRepository mongoRepo,
                                CacheRedis cache,
                                ObjectMapper mapper) {
        this.sqlRepo = sqlRepo;
        this.mongoRepo = mongoRepo;
        this.cache = cache;
        this.mapper = mapper;
    }

    public void crearPublicacion(PublicacionSql sql) {
        logger.info("📝 Creando publicación en PostgreSQL: {}", sql.getTitulo());
        PublicacionSql guardada = sqlRepo.save(sql);
        
        // Propagar a MongoDB
        PublicacionMongo mongo = new PublicacionMongo(
            sql.getTitulo(), sql.getAutores(), sql.getAnio(), sql.getDoi()
        );
        mongo.setId(guardada.getId().toString());
        mongoRepo.save(mongo);
        logger.info("📝 Replicado a MongoDB");
        
        // Actualizar caché
        try {
            cache.set(sql.getDoi(), mapper.writeValueAsString(guardada));
            logger.info("📝 Actualizado caché Redis");
        } catch (Exception e) {
            logger.error("❌ Error actualizando caché", e);
        }
    }

    public PublicacionSql obtenerPublicacion(String doi) {
        // Intenta caché primero
        try {
            String cached = cache.get(doi);
            if (cached != null) {
                logger.info("✅ Recuperado de caché para DOI: {}", doi);
                return mapper.readValue(cached, PublicacionSql.class);
            }
        } catch (Exception e) {
            logger.warn("⚠️ Error leyendo caché", e);
        }
        
        // Falla a PostgreSQL
        PublicacionSql sql = sqlRepo.findByDoi(doi);
        if (sql != null) {
            logger.info("✅ Recuperado de PostgreSQL para DOI: {}", doi);
            // Actualizar caché
            try {
                cache.set(doi, mapper.writeValueAsString(sql));
            } catch (Exception e) {
                logger.warn("⚠️ Error actualizando caché", e);
            }
        }
        return sql;
    }

    public void sincronizarDesdePostgres() {
        logger.info("🔄 Sincronizando desde PostgreSQL a MongoDB/Redis");
        sqlRepo.findAll().forEach(sql -> {
            PublicacionMongo mongo = mongoRepo.findByDoi(sql.getDoi());
            if (mongo == null) {
                mongo = new PublicacionMongo(sql.getTitulo(), sql.getAutores(), sql.getAnio(), sql.getDoi());
                mongo.setId(sql.getId().toString());
            } else {
                mongo.setTitulo(sql.getTitulo());
                mongo.setAutores(sql.getAutores());
                mongo.setAnio(sql.getAnio());
            }
            mongoRepo.save(mongo);
        });
        logger.info("✅ Sincronización completada");
    }
}
