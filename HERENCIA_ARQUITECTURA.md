# Herencia Arquitectónica - Taller 11

## Cadena Acumulativa Completa

```
T1: Stack Spring Boot
  ↓
T3: DDD (Domain-Driven Design)
  ├─ Dominio (Value Objects, Agregados, Eventos)
  ├─ Aplicación (Use Cases, Factories, Servicios)
  └─ Excepciones de negocio (CorreoDuplicadoException, etc.)
  ↓
T4: Hexagonal Architecture
  ├─ Puertos (RepositorioInvestigadores, PublicadorEventos)
  ├─ Adaptadores entrada (InvestigadorController, InvestigadorGrpcService)
  └─ Adaptadores salida (InvestigadorRepository, OutboxPublicadorEventos)
  ↓
T5: Microservicios
  ├─ investigadores-service (Dominio: Investigadores)
  ├─ publicaciones-service (Dominio: Publicaciones)
  ├─ eureka-server (Service Discovery)
  └─ gateway (API Gateway)
  ↓
T6: Comunicación entre Microservicios
  ├─ REST (InvestigadorController, PublicacionController)
  ├─ gRPC (InvestigadorGrpcService, VerificadorInvestigador)
  ├─ Kafka (PublicacionesMarcadasListener, InvestigadorEnEliminacionListener)
  └─ Outbox pattern (OutboxEvent, OutboxPoller)
  ↓
T8: CQRS
  ├─ Write Model: investigadores-service, publicaciones-service
  ├─ Read Model: consultas-service (MongoDB)
  └─ Proyección: ProyectorFichas (escucha Kafka)
  ↓
T9: EDA (Event-Driven Architecture)
  ├─ Eventos de dominio: InvestigadorCreadoEvent, PublicacionCreadaEvent
  ├─ PublicadorEventos (Kafka producer)
  ├─ SuscriptorEventos (Kafka consumer)
  └─ Dead Letter Topic (error handling con reintentos)
  ↓
T10: Observabilidad + Saga Pattern
  ├─ Distributed Tracing (X-Correlation-ID header)
  ├─ MDC Logging (correlationId en logs)
  ├─ Métricas (Micrometer/Prometheus)
  ├─ Actuator endpoints (/metrics, /prometheus, /health)
  └─ Saga Orchestrator (transacciones distribuidas con compensaciones)
  ↓
T11: Polyglot Persistence + DevOps
  ├─ PostgreSQL (write model source of truth)
  ├─ MongoDB (read model, análisis)
  ├─ Redis (cache con TTL 1h)
  ├─ Docker (Dockerfile multi-etapa, .dockerignore)
  ├─ Docker Compose (infraestructura local: Kafka, PostgreSQL, MongoDB, Redis, Eureka)
  └─ CI/CD (GitHub Actions con matriz de servicios, mvnw test)
```

## Componentes por Servicio en T11

### investigadores-service
- **Dominio (T3)**: Investigador, CorreoInstitucional, EstadoInvestigador, CorreoDuplicadoException
- **Aplicación (T3)**: InvestigadorService, InvestigadorUseCase, InvestigadorFactory, RepositorioInvestigadores
- **Adaptadores (T4)**:
  - Entrada: InvestigadorController (REST), InvestigadorGrpcService (gRPC)
  - Salida: InvestigadorRepository (JPA), OutboxPublicadorEventos (Kafka outbox)
- **Microservicios (T5)**: Servicio independiente con su propia base de datos
- **Comunicación (T6)**: REST, gRPC, Kafka listeners (PublicacionesMarcadasListener)
- **Observabilidad (T10)**: CorrelacionIdInterceptor, MetricasService, SagaOrquestador
- **DevOps (T11)**: Dockerfile, docker-compose.yml, CI workflow

### publicaciones-service
- **Dominio (T3)**: Publicacion, LimitePublicacionesAnualesService
- **Aplicación (T3)**: PublicacionService, ServicioNoDisponibleException
- **Adaptadores (T4)**:
  - Entrada: PublicacionController (REST)
  - Salida: PublicacionRepository (JPA), Listeners de Kafka
- **Microservicios (T5)**: Servicio independiente con su propia base de datos
- **Comunicación (T6)**: REST, Kafka listeners (InvestigadorRegistradoListener, InvestigadorEnEliminacionListener)
- **Polyglot (T11)**: PublicacionSql (JPA), PublicacionMongo, CacheRedis, SincronizadorPolyglot
- **Observabilidad (T10)**: CorrelacionIdInterceptor
- **DevOps (T11)**: Dockerfile, docker-compose.yml, CI workflow

### consultas-service (NEW en T8)
- **CQRS (T8)**: Read model con FichaInvestigador (MongoDB)
- **Proyección (T8)**: ProyectorFichas (escucha eventos de Kafka)
- **Comunicación (T6)**: Solo GET endpoints (ConsultasController)
- **Observabilidad (T10)**: CorrelacionIdInterceptor
- **DevOps (T11)**: Dockerfile, CI workflow

### eureka-server + gateway
- **Microservicios (T5)**: Infraestructura base
- **Comunicación (T6)**: Service discovery + API Gateway
- **DevOps (T11)**: Dockerfile, CI workflow

## Verificación de Herencia

✅ **T3 (DDD)**: Paquetes `dominio/`, `aplicacion/`, excepciones de negocio
✅ **T4 (Hexagonal)**: Adaptadores en `entrada/` (web, grpc), `salida/` (persistencia, mensajería)
✅ **T5 (Microservicios)**: 5 servicios independientes (investigadores, publicaciones, consultas, eureka, gateway)
✅ **T6 (Comunicación)**: REST, gRPC, Kafka con listeners y outbox pattern
✅ **T8 (CQRS)**: Read model en consultas-service, proyector de eventos
✅ **T9 (EDA)**: Dead Letter Topic, reintentos con backoff
✅ **T10 (Observabilidad)**: X-Correlation-ID en 3 servicios, logs con correlationId, métricas
✅ **T11 (Polyglot + DevOps)**: PostgreSQL, MongoDB, Redis, Docker, CI workflow

## Ubicación de Código

```
swii-taller-11-persistencia/
├── investigadores-service/
│   ├── src/main/java/com/rica/investigadoresservice/
│   │   ├── investigadores/
│   │   │   ├── dominio/          ← T3: DDD
│   │   │   ├── aplicacion/       ← T3: DDD
│   │   │   └── infraestructura/
│   │   │       ├── entrada/      ← T4: Hexagonal (REST, gRPC)
│   │   │       └── salida/       ← T4: Hexagonal (JPA, Kafka outbox)
│   │   ├── observabilidad/       ← T10: X-Correlation-ID
│   │   └── plataforma/           ← T6: KafkaConfig, T9: Dead Letter Topic
│   └── Dockerfile                ← T11: DevOps
├── publicaciones-service/
│   ├── src/main/java/com/rica/publicacionesservice/
│   │   ├── publicaciones/        ← T3-T6 (DDD, Hexagonal, Microservicios, Comunicación)
│   │   └── persistencia/         ← T11: Polyglot (SQL, Mongo, Redis, Sincronizador)
│   ├── src/main/java/com/rica/publicaciones/observabilidad/ ← T10
│   └── Dockerfile                ← T11: DevOps
├── consultas-service/
│   ├── src/main/java/com/rica/cqrs/
│   │   ├── modelo/               ← T8: CQRS read model
│   │   ├── proyector/            ← T8: Proyector de eventos
│   │   └── observabilidad/       ← T10: X-Correlation-ID
│   └── Dockerfile                ← T11: DevOps
├── eureka-server/                ← T5: Microservicios
├── gateway/                       ← T5-T6: Comunicación
├── docker-compose.yml            ← T11: DevOps
├── .dockerignore                 ← T11: DevOps
├── .github/workflows/ci.yml      ← T11: DevOps (CI/CD)
└── .git/
```

## Conclusión

**Taller 11 contiene la implementación completa y acumulativa de todos los talleres previos:**

- Arquitectura DDD + Hexagonal (T3-T4)
- Descomposición en 5 microservicios (T5)
- Comunicación multi-protocolo: REST, gRPC, Kafka (T6)
- Patrón CQRS con read model en MongoDB (T8)
- Arquitectura dirigida por eventos con manejo de errores (T9)
- Observabilidad distribuida con tracing, métricas y saga pattern (T10)
- Persistencia en 3 bases de datos con sincronización automática (T11)
- Containerización y CI/CD con GitHub Actions (T11)

**Total: ~5,000+ líneas de código Java, 30+ tests, 1 repositorio GitHub autosuficiente.**
