# Informe de verificacion (talleres)

- Repositorio: swii-taller-11-persistencia
- Commit: bb5cf4185f8d7ae456075bcb6fa12983738aa25e
- Rama actual: master (fecha del commit: 2026-11-11T23:41:01-05:00)
- **Huella del informe: d710691b5429**  (dos evaluadores del mismo commit deben obtener la misma huella)

## Talleres 3 a 9 (parte automatica)

| ID | Criterio | Puntos | Obtenido | Evidencia |
|---|---|---|---|---|
| T3a | Taller 3 (DDD): paquete de dominio con al menos 3 clases | 3 | 0 | 0 clases |
| T3b | Taller 3: existe una Factory | 3 | 0 | ninguno |
| T3c | Taller 3: hay un Value Object (record, @Embeddable o clase de valor) | 2 | 0 | ninguno |
| T3d | Taller 3: pruebas unitarias del dominio | 2 | 0 | 0 clases de prueba |
| T4a | Taller 4 (hexagonal): paquete de puertos | 3 | 0 | 0 archivos |
| T4b | Taller 4: adaptadores o infraestructura separados | 3 | 0 | 0 archivos |
| T4c | Taller 4: el dominio no importa Spring | 2 | 0 | limpio |
| T5a | Taller 5: al menos 2 servicios independientes con puerto propio | 4 | 0 | 1 servicios; 1 puertos distintos |
| T5b | Taller 5: NOTAS.md con la auditoria | 5 | 5 | 111 palabras |
| T6a | Taller 6: gRPC (archivo .proto) | 3 | 0 | ninguno |
| T6b | Taller 6: Circuit Breaker | 3 | 0 | ninguno |
| T6c | Taller 6: Eureka (servidor y clientes) | 2 | 0 | 0 pom con Eureka |
| T6d | Taller 6: API Gateway | 2 | 0 | ninguno |
| T6e | Taller 6: Kafka con productor, consumidor y Outbox | 2 | 0 | consumidores 0; productores 0 |
| T8a | Taller 8 (CQRS): modelo de lectura o proyector | 4 | 0 | ninguno |
| T8b | Taller 8: un servicio consume eventos con @KafkaListener | 3 | 0 | 0 clases |
| T8c | Taller 8: pruebas del proyector o de la ficha | 3 | 0 | 0 clases de prueba |
| T9a | Taller 9 (EDA): Dead Letter Topic | 4 | 0 | ninguno |
| T9b | Taller 9: consumidor idempotente | 4 | 4 | src/main/java/com/rica/persistence/mongo/OrderEventLog.java, src/main/java/com/rica/persistence/mongo/OrderEventLogRepository.java |
| T9c | Taller 9: reintentos con espera (backoff) | 3 | 0 | ninguno |

**Total automatico: 9 / 60**

## Talleres 10 y 11 (bonus opcional)

| ID | Criterio | Puntos | Obtenido | Evidencia |
|---|---|---|---|---|
| T10a | Taller 10 (observabilidad): Actuator en al menos 3 servicios | 1 | 0 | 0 servicios |
| T10b | Taller 10: filtro con X-Correlation-Id | 1 | 0 | ninguno |
| T10c | Taller 10: patron de log con correlationId en al menos 3 servicios | 1 | 0 | 0 archivos yml |
| T10d | Taller 10: el correlationId viaja dentro de los eventos | 1 | 0 | ninguno |
| T11a | Taller 11: Dockerfile multi-etapa | 1 | 0 | ninguno |
| T11b | Taller 11: .dockerignore | 1 | 0 |  |
| T11c | Taller 11: docker-compose.yml | 1 | 1 |  |
| T11d | Taller 11: CI con matriz de servicios | 2 | 0 | ninguno |
| T11e | Taller 11: el CI ejecuta las pruebas con mvnw | 1 | 0 | ninguno |

**Bonus: 1 / 10**

## Aporte por integrante (de git log)

| Autor | Commits | Archivos distintos | Lineas + | Lineas - |
|---|---|---|---|---|
| Ingeniería Software II | 1 | 5 | 223 | 0 |

## Avisos (no puntuan)

- ATENCION: hay cambios sin confirmar. El informe describe el repositorio tal como esta en disco, no el commit. Para evaluar el commit use un clon limpio.

La parte manual de la rubrica (criterios MT) se completa en Moodle con la guia de evaluacion.
