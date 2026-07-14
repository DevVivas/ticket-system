# AGENTS.md

## Project Structure

Microservices monorepo: 12 independent Spring Boot services + API Gateway for a ticket/event management system. Each service lives in a **double-nested directory** pattern: `ms-<name>/<name>/` (e.g., `ms-artistas/artistas/`). The actual Maven project is inside the second level.

**No root `pom.xml` exists.** Each microservice is a standalone Maven project with its own `spring-boot-starter-parent` (4.0.6).

## Build & Run

### Full stack (Docker Compose)
```bash
docker compose up -d --build
```
Starts all 17 containers: MySQL, 12 microservices, API Gateway, Prometheus, Grafana, Loki, Promtail.

### Single microservice (Maven)
```bash
cd ms-artistas/artistas
./mvnw spring-boot:run        # Unix
mvnw.cmd spring-boot:run      # Windows
```
Requires MySQL on `localhost:3306` (or `localhost:3307` if using Docker MySQL).

### All services locally (Windows)
```bash
iniciar-todo.bat
```
Opens 12 separate `cmd` windows, one per microservice.

## Testing

```bash
cd ms-artistas/artistas
./mvnw test                    # Unit tests only
./mvnw test jacoco:report      # Tests + coverage report
./mvnw clean verify            # Tests + JaCoCo coverage gate (60% minimum, ms-artistas only)
```

Tests use JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`). Integration test stubs (`*ApplicationTests.java`) exist but most services only have unit tests. `ms-auth` and `ms-api-gateway` have **no tests**.

## CI/CD

Only `ms-artistas` has GitHub Actions pipelines (`.github/workflows/`). Two workflow files:
- `ci-cd.yml` — general pipeline (hardcoded to `ms-artistas/artistas`)
- `ms-artistas-ci.yml` — dedicated pipeline with 4 jobs: build, test+JaCoCo, SonarCloud, Docker push

Other microservices have **no CI/CD**.

## Port Assignments

| Service | Port | Database |
|---------|------|----------|
| ms-api-gateway | 8080 | — |
| ms-eventos | 8081 | db_eventos |
| ms-recintos | 8082 | db_recintos |
| ms-tickets | 8083 | db_tickets |
| ms-ventas | 8084 | db_ventas |
| ms-validacion | 8085 | db_validacion |
| ms-artistas | 8086 | db_artistas |
| ms-preventa | 8087 | db_preventa |
| ms-devoluciones | 8088 | db_devoluciones |
| ms-promotores | 8089 | db_promotores |
| ms-streaming | 8090 | db_streaming |
| ms-auth | 8091 | db_auth |

Infrastructure: MySQL 3307, Prometheus 9090, Grafana 3000 (admin/admin), Loki 3100.

## Architecture

- **API Gateway** (`ms-api-gateway`, port 8080) routes `/api/<service>/**` to each microservice
- **Database-per-service**: single MySQL 8.0 instance, each service owns its own database
- **Flyway** manages schema via `V1__init.sql` in each service. `ddl-auto=validate` in most services; `ms-auth` uses `update` (inconsistent)
- **Inter-service calls** use `WebClient` (Spring WebFlux). Service URLs configured via `MS_*_URL` env vars
- **Observability**: Prometheus scrapes `/actuator/prometheus`; Loki + Promtail collect Docker logs; Grafana auto-provisions dashboards

## Quirks & Gotchas

- **Stale duplicate directories**: `ms-streaming/streaming/streaming/` and `ms-promotores/promotores/promotores/` contain abandoned copies with their own `pom.xml` and `src/`. Do not edit these.
- **Double-dot test filename**: `ms-artistas/.../ArtistaServiceTest..java` has two dots — likely a typo artifact
- **`.gitignore` is mostly commented out** — compiled artifacts (`target/`, `*.class`) may be tracked in git
- **Duplicate dependencies** in some `pom.xml` files (Flyway in ms-eventos, Actuator in ms-artistas)
- **Hardcoded JWT secret** in `application.properties` and `docker-compose.yml` — fine for dev, not production
- **No `.env` files** — all config is inline in `docker-compose.yml` or `application.properties` defaults
- **SonarCloud exclusions**: `**/Model/**`, `**/DTO/**`, `**/*Application.java`
