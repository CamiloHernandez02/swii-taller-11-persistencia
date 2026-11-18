# TALLER 11: Polyglot Persistence + CI/CD

## Objetivos
- Implementar Polyglot Persistence (múltiples bases de datos optimizadas por patrón de acceso)
- Usar PostgreSQL como source of truth
- Usar MongoDB para acceso flexible
- Usar Redis para caché de alto rendimiento
- Configurar sincronización automática
- Preparar CI/CD con Docker y orquestación

## Arquitectura Polyglot Persistence

```
┌────────────────────────────────────────────────────────────────┐
│                 Polyglot Persistence Architecture              │
├────────────────────────────────────────────────────────────────┤
│
│  Write Path (Write-Through):
│  ┌───────┐ POST /publicaciones ┌──────────────────┐
│  │Client├──────────────────────►PublicacionService│
│  └───────┘                      └────────┬─────────┘
│                                           │
│                    ┌──────────────────────┼──────────────────┐
│                    │                      │                  │
│                    ▼                      ▼                  ▼
│            ┌──────────────┐      ┌───────────────┐  ┌──────────────┐
│            │PostgreSQL    │      │MongoDB        │  │Redis         │
│            │(Source Truth)│      │(Flexible)     │  │(Cache, TTL)  │
│            └──────────────┘      └───────────────┘  └──────────────┘
│                 ✓ ACID              Document Store    1h TTL
│                 ✓ Constraints       Schema-less        O(1) GET
│
│  Read Path (Cache-Aside):
│  ┌───────┐ GET /publicaciones/{id}
│  │Client │
│  └───┬───┘
│      │
│      ▼
│   Redis Cache? ──YES──► Return (cached)
│      │
│      NO
│      │
│      ▼
│   PostgreSQL ──► Return + cache
│
│  Sincronización:
│  - Write a SQL: automático a MongoDB + Redis
│  - Delete en SQL: automático eliminar de MongoDB + Redis
│  - Invalidación: TTL en Redis (1 hora)
```

## Concepto 1: Polyglot Persistence

### ¿Por qué múltiples bases de datos?

```
Acceso                    Storage Óptimo
─────────────────────────────────────────
Write: Transacciones      PostgreSQL (ACID)
Read: Por ID              Redis (caché)
Read: Búsquedas           MongoDB (índices flexible)
Full-text search          Elasticsearch (invertido)
Tiempo real               Kafka streams
Histórico                 Data Lake (S3)
```

### Strategy: Write-Through
```
Guardas en PostgreSQL
       ↓
Se propaga a MongoDB
       ↓
Se propaga a Redis
       ↓
Escritura completada
```

**Ventajas**: Consistencia garantizada, fácil de debuggear
**Desventajas**: Write latency más alta

### Strategy: Read Consistency
```
GET /pub/pub-001
      ↓
¿En Redis? → SÍ → Retornar (latencia: 1ms)
      │
      NO (cache miss)
      ↓
¿En PostgreSQL? → SÍ → Retornar + guardar en Redis
      │
      NO
      ↓
Retornar 404
```

## Concepto 2: Componentes en T11

### PublicacionSql (JPA Entity)
```java
@Entity
@Table(name = "publicaciones")
public class PublicacionSql {
    @Id
    private String id;
    private String investigadorId;
    private String titulo;
    @Column(unique = true)
    private String doi;
    private int año;
    @Version
    private Long version;  // Optimistic locking
}
```
- PostgreSQL
- ACID guarantees
- Constraints (unique DOI)
- Version field para evitar lost updates

### PublicacionMongo (MongoDB Document)
```java
@Document(collection = "publicaciones")
public class PublicacionMongo {
    @Id
    private String id;
    private String investigadorId;
    private String titulo;
    private String doi;
    private int año;
    private long timestamp;
}
```
- Schema-less
- Fácil búsquedas por investigador (agregaciones)
- Desnormalizado para acceso rápido

### CacheRedis (Cache TTL)
```java
@Service
public class CacheRedis {
    private static final String CACHE_PREFIX = "pub:";
    private static final long TTL_HORAS = 1;
    
    public void guardar(PublicacionSql pub) {
        redisTemplate.opsForValue().set(
            CACHE_PREFIX + pub.getId(),
            json(pub),
            1, TimeUnit.HOURS
        );
    }
}
```
- Almacenamiento clave-valor
- TTL automático (expires after 1 hour)
- O(1) lookup por ID

### SincronizadorPolyglot (Orquestador)
```java
@Service
public class SincronizadorPolyglot {
    public PublicacionSql guardar(PublicacionSql pub) {
        // 1. Write-Through: PostgreSQL primero
        pub = sqlRepo.save(pub);
        
        // 2. Propagar a MongoDB (async es posible en T12+)
        mongoRepo.save(toMongo(pub));
        
        // 3. Propagar a Redis
        cache.guardar(pub);
        
        return pub;
    }
    
    public Optional<PublicacionSql> obtener(String id) {
        // Cache-aside pattern
        PublicacionSql cached = cache.obtener(id);
        if (cached != null) return Optional.of(cached);
        
        // Fallback a SQL
        Optional<PublicacionSql> result = sqlRepo.findById(id);
        result.ifPresent(cache::guardar);  // Repopular
        return result;
    }
}
```

## Servicios en T11

Todos los servicios de T10 (Observabilidad + Saga) PLUS:

### publicaciones-service (Mejorado)
- **Nuevo**: `PublicacionSql` (JPA Entity en PostgreSQL)
- **Nuevo**: `PublicacionMongo` (MongoDB Document)
- **Nuevo**: `CacheRedis` (Redis con TTL 1h)
- **Nuevo**: `SincronizadorPolyglot` (Orquestador)
- **Nuevo**: `PublicacionSqlRepository` (JPA)
- **Nuevo**: `PublicacionMongoRepository` (Spring Data MongoDB)

### Otros servicios (T10)
- Continúan igual (investigadores-service, consultas-service, etc.)

## Configuración T11

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/rica_db
    username: rica
    password: rica123
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        
  data:
    mongodb:
      uri: mongodb://ricaadmin:ricapass123@localhost:27017/rica_db
      
  redis:
    host: localhost
    port: 6379
    timeout: 60000
    jedis:
      pool:
        max-active: 8
```

## Docker Compose (Infraestructura Completa)

```bash
docker-compose up -d

# Servicios:
# - PostgreSQL: localhost:5432
# - MongoDB: localhost:27017
# - Redis: localhost:6379
# - Kafka: localhost:9092
# - Zookeeper: localhost:2181
# - Prometheus: localhost:9090
```

### Conexiones de Base de Datos

```bash
# PostgreSQL
psql -h localhost -U rica -d rica_db

# MongoDB
mongosh mongodb://ricaadmin:ricapass123@localhost:27017

# Redis
redis-cli
```

## Tests en T11

**publicaciones-service:**
- `CacheRedisTest` (2 tests)
  - testGuardarEnCache
  - testObtenerDelCache
  
- `SincronizadorPolyglotTest` (2 tests)
  - testGuardarPropagaATodosLosStorages
  - testObtenerDelCacheConFallback

## Flujo End-to-End Completo (T8-T11)

```
1. Cliente registra investigador
   POST /investigadores → investigadores-service (SQL)
   ├─ Publica InvestigadorCreadoEvent
   └─ X-Correlation-ID: xyz-789

2. Cliente registra publicación
   POST /publicaciones → publicaciones-service
   ├─ Guarda en PostgreSQL (Write-Through)
   ├─ Propaga a MongoDB
   ├─ Propaga a Redis
   ├─ Publica PublicacionCreadaEvent
   ├─ Registra métrica: http.requests.total += 1
   └─ Propaga X-Correlation-ID

3. Consulta-service escucha evento
   KafkaListener → ProyectorFichas
   ├─ Recibe PublicacionRegistradaEvent
   ├─ Proyecta a MongoDB (Read Model)
   └─ FichaInvestigador se actualiza

4. Cliente consulta fichas (lectura rápida)
   GET /fichas/{id} → consultas-service (MongoDB)
   ├─ Lectura desde CQRS Read Model
   ├─ Respuesta en 10-50ms
   └─ Retorna X-Correlation-ID

5. Cliente consulta publicaciones
   GET /publicaciones/{id} → publicaciones-service
   ├─ Intenta Redis (cache) → HIT (1ms)
   ├─ Si no: Intenta PostgreSQL → Repopula Redis
   └─ Retorna

6. Monitoreo y Observabilidad
   curl http://localhost:9090/actuator/prometheus
   ├─ Métricas de requests
   ├─ Latencias (p50, p95, p99)
   └─ Error rates
```

## Próximas Fases

- **T12**: ADR (Architecture Decision Records) documentando decisiones
- **T13**: Cierre y evaluación del proyecto

### Futuras Mejoras (Beyond T11)
- **Async Propagation**: Propagar a MongoDB/Redis de forma asincrónica (colas)
- **Event Sourcing**: Guardar todos los eventos (audit trail)
- **CQRS Level 3**: Histórico de proyecciones
- **Saga Distribuida**: Orquestación entre múltiples servicios
- **API Gateway**: Enrutamiento centralizado
- **Service Mesh**: Istio para observabilidad avanzada
- **Multi-DC Replication**: Replicación entre data centers

## Decisiones Arquitectónicas

### Write-Through vs Write-Behind
- **Write-Through** (actual): Garantía inmediata de consistencia
- **Write-Behind**: Write a cache, después persist (riesgo de pérdida)
- T11 elige Write-Through por ACID requirements

### SQL vs NoSQL
- **SQL**: Fuerza ACID, integridad referencial, constraints
- **NoSQL**: Flexible, escalable, consultas ad-hoc
- Estrategia: SQL como "source of truth", NoSQL como "read optimized"

### Redis TTL
- **1 Hora**: Balance entre freshness y cache efficiency
- **Sin TTL**: Riesgo de stale data (need invalidation on update)
- **Muy corto**: Muchos cache misses, overhead

## Troubleshooting

| Problema | Solución |
|----------|----------|
| PostgreSQL won't start | `docker-compose logs postgres`, revisar volumen |
| MongoDB connection refused | `mongosh` puede no estar instalado, usar `mongo` |
| Redis cache no funciona | Revisar `redis-cli KEYS pub:*` |
| Sincronización falla | Revisar logs, uno de los 3 storage falló |
| Métrica no aparece | Acceder a `/actuator/metrics` para registrar primero |

## Referencias
- Polyglot Persistence: https://www.nginx.com/resources/glossary/polyglot-persistence/
- PostgreSQL: https://www.postgresql.org/
- MongoDB: https://www.mongodb.com/
- Redis: https://redis.io/
- Docker Compose: https://docs.docker.com/compose/
- Prometheus: https://prometheus.io/
