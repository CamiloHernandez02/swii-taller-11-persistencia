# Taller 11: Polyglot Persistence + CI/CD

Ingeniería de Software II (UPTC). Implementación de Polyglot Persistence con sincronización automática entre PostgreSQL, MongoDB y Redis.

**Base**: Taller 10 (Observabilidad + Saga)
**Nuevo**: Taller 11 agrega persistencia políglota en publicaciones-service

| Pieza | Proyecto | Puerto | Storage |
| :--- | :--- | :--- | :--- |
| Registro de servicios (Eureka) | `eureka-server` | 8761 | - |
| API Gateway | `gateway` | 8000 | - |
| Investigadores | `investigadores-service` | 8081 | PostgreSQL |
| Publicaciones | `publicaciones-service` | 8082 | PostgreSQL + MongoDB + Redis |
| Consultas | `consultas-service` | 8084 | MongoDB |
| Prometheus | (docker) | 9090 | - |
| PostgreSQL | (docker) | 5432 | - |
| MongoDB | (docker) | 27017 | - |
| Redis | (docker) | 6379 | - |
| Kafka | (docker) | 9092 | - |

## Características T11: Polyglot Persistence
- ✅ PublicacionSql (JPA Entity en PostgreSQL, source of truth)
- ✅ PublicacionMongo (Document en MongoDB)
- ✅ CacheRedis (clave-valor con TTL 1 hora)
- ✅ SincronizadorPolyglot (write-through, read cache-aside)
- ✅ Sincronización automática a 3 storages
- ✅ Fallback en caso de cache miss
- ✅ docker-compose.yml completo
- ✅ Tests: 4 (2 CacheRedis + 2 SincronizadorPolyglot)

Patrones: gRPC (`VerificadorInvestigadorGrpc`), Circuit Breaker (Resilience4j), Service Registry (Eureka), API Gateway, Domain Event + Transactional Outbox (Kafka), Saga por coreografía (eliminar investigador).

## Ejecutar

```bash
cd investigadores-service && docker compose up -d      # PostgreSQL + Kafka
cd publicaciones-service && docker compose up -d       # MongoDB
# en terminales separadas, en este orden:
cd eureka-server && ./mvnw spring-boot:run
cd investigadores-service && ./mvnw spring-boot:run
cd publicaciones-service && ./mvnw spring-boot:run
cd gateway && ./mvnw spring-boot:run
```

Los servicios se consumen por el Gateway: `http://localhost:8000/api/investigadores` y `http://localhost:8000/api/publicaciones`.

Ver [NOTAS.md](NOTAS.md) para la verificación y las decisiones.
