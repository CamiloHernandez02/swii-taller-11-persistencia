# Informe de verificacion (proyecto)

- Repositorio: swii-taller-11-persistencia
- Commit: bb5cf4185f8d7ae456075bcb6fa12983738aa25e
- Rama actual: master (fecha del commit: 2026-11-11T23:41:01-05:00)
- **Huella del informe: 0ce1b1b9cd4d**  (dos evaluadores del mismo commit deben obtener la misma huella)

## Proyecto (parte automatica)

| ID | Criterio | Puntos | Obtenido | Evidencia |
|---|---|---|---|---|
| A1 | Al menos 3 servicios Spring Boot (modulos con pom.xml) | 4 | 0 | 1 encontrados: (raiz) |
| A2 | Al menos la mitad de los servicios tienen una clase de prueba con @Test | 4 | 4 | 1 de 1 con pruebas |
| A3 | Las pruebas pasan en todos los servicios (mvnw test) | 8 | 0 | NO EJECUTADO: use --pruebas |
| A4 | Hay un docker-compose.yml para la infraestructura (base de datos, broker) | 4 | 4 | si |
| B1 | Capas separadas (dominio / aplicacion / infraestructura) en al menos 2 servicios | 5 | 0 | 0 servicios:  |
| B2 | Servicios independientes: al menos 2, cada uno con su propio puerto | 4 | 0 | 1 puertos declarados, 1 distintos |
| B3 | Comunicacion entre servicios (HTTP, gRPC o mensajeria) en al menos 2 servicios | 5 | 0 | 1 servicios: (raiz) |
| B4 | Persistencia propia declarada en al menos 2 servicios | 3 | 0 | 1 servicios: (raiz) |
| B5 | Resiliencia o consistencia (circuit breaker, reintentos, outbox, saga o idempotencia) en el codigo | 4 | 4 | 1 archivos: VerificarProyecto.java |
| B6 | API Gateway o descubrimiento de servicios (Eureka) | 4 | 0 | ninguno |
| C1 | Existe ARQUITECTURA.md con contenido (al menos 200 palabras) | 3 | 0 | no existe |
| C2 | Al menos 3 ADR, cada uno con 'alternativa' y 'consecuencias' | 5 | 0 | 0 ADR validos de 0 encontrados |
| C3 | ARQUITECTURA.md incluye un diagrama (imagen o mermaid) | 2 | 0 | no |
| C4 | ATAM-LITE.md (3 riesgos o mas) y MATRIZ.md (tabla de 6 filas o mas) | 3 | 0 | atam: no; matriz: no |
| C5 | GOBERNANZA.md, CASO.md y RESUMEN-EJECUTIVO.md (60 palabras o mas cada uno) | 2 | 0 | gobernanza no; caso no; resumen no |
| D1 | Todos los integrantes tienen al menos 5 commits propios | 4 | 0 | 0 autores con 5 o mas commits; esperados 1 |
| D2 | REPARTO.md existe y menciona a cada autor con 5 o mas commits | 3 | 0 | no existe |
| D3 | Aporte equilibrado: nadie por debajo de la mitad de su parte igual de commits | 3 | 0 | minimo exigido 0.0 commits por integrante |

**Total automatico: 12 / 70**

## Aporte por integrante (de git log)

| Autor | Commits | Archivos distintos | Lineas + | Lineas - |
|---|---|---|---|---|
| Ingeniería Software II | 1 | 5 | 223 | 0 |

## Avisos (no puntuan)

- ATENCION: hay cambios sin confirmar. El informe describe el repositorio tal como esta en disco, no el commit. Para evaluar el commit use un clon limpio.
- A3 no se evaluo: ejecute de nuevo con --pruebas (tarda unos minutos).
- No se indico --integrantes: se asumio 1 (autores con commits). Indique el numero real para evaluar D1 a D3.
- REVISAR: el historial tiene solo 1 commits.

La parte manual de la rubrica (criterios M) se completa en Moodle con la guia de evaluacion.
