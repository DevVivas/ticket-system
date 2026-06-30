# 🎤 MS-Artistas

Microservicio de **gestión de perfiles de artistas y sus agendas de presentaciones**, perteneciente al ecosistema **Ticket System** (arquitectura de microservicios para venta y administración de entradas a eventos).

[![CI/CD](https://github.com/DevVivas/ticket-system-ms-artistas/actions/workflows/ms-artistas-ci.yml/badge.svg)](https://github.com/DevVivas/ticket-system-ms-artistas/actions/workflows/ms-artistas-ci.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=DevVivas_ticket-system-ms-artistas&metric=alert_status)](https://sonarcloud.io/dashboard?id=DevVivas_ticket-system-ms-artistas)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DevVivas_ticket-system-ms-artistas&metric=coverage)](https://sonarcloud.io/dashboard?id=DevVivas_ticket-system-ms-artistas)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-Educational-blue)](#-licencia)

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura](#-arquitectura)
- [Stack Tecnológico](#-stack-tecnológico)
- [Modelo de Datos](#-modelo-de-datos)
- [API REST](#-api-rest)
- [Configuración](#-configuración)
- [Cómo Ejecutar](#-cómo-ejecutar)
- [Testing y Cobertura](#-testing-y-cobertura)
- [CI/CD](#-cicd)
- [Calidad de Código](#-calidad-de-código)
- [Observabilidad](#-observabilidad)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Equipo](#-equipo)

---

## 📖 Descripción

`ms-artistas` administra el ciclo de vida de los **artistas** participantes en el sistema de ticketing: datos de perfil (nombre, género musical, nacionalidad, biografía), estado (activo/inactivo) y su **agenda de presentaciones**, incluyendo la confirmación o cancelación de cada fecha agendada.

Se comunica con `ms-eventos` vía `WebClient` para enriquecer o validar información relacionada a los eventos en los que participa un artista.

---

## 🏗️ Arquitectura

```
                     ┌──────────────────┐
                     │  ms-api-gateway  │
                     └────────┬─────────┘
                              │
                              ▼
                     ┌──────────────────┐         ┌──────────────┐
                     │   ms-artistas    │ ───────▶│  ms-eventos   │
                     │   (puerto 8086)  │ WebClient│ (puerto 8081) │
                     └────────┬─────────┘         └──────────────┘
                              │
                ┌─────────────┼─────────────┐
                ▼             ▼             ▼
            MySQL        Prometheus       Loki
         (db_artistas)   (métricas)      (logs)
```

---

## 🛠️ Stack Tecnológico

| Categoría        | Tecnología                                              |
|-------------------|----------------------------------------------------------|
| Lenguaje          | Java 21                                                   |
| Framework         | Spring Boot 4.0.6 (Web, Data JPA, Validation, WebFlux)    |
| Base de datos     | MySQL 8 + Flyway (migraciones versionadas)                |
| Productividad     | Lombok                                                    |
| Build             | Maven (Maven Wrapper incluido)                             |
| Contenedores      | Docker (build multi-stage)                                |
| CI/CD             | GitHub Actions                                            |
| Calidad de código | SonarCloud + JaCoCo                                       |
| Métricas          | Spring Actuator + Micrometer + Prometheus                 |
| Logs              | Logback (JSON estructurado) + Promtail + Loki              |
| Dashboards        | Grafana                                                    |

---

## 🗃️ Modelo de Datos

**`artistas`**

| Campo            | Tipo          | Descripción                              |
|-------------------|---------------|-------------------------------------------|
| `id`              | BIGINT (PK)   | Identificador autoincremental             |
| `nombre`          | VARCHAR(150)  | Nombre del artista                        |
| `genero`          | VARCHAR(255)  | Género musical                            |
| `nacionalidad`    | VARCHAR(255)  | Nacionalidad                              |
| `biografia`       | TEXT          | Biografía (opcional)                      |
| `imagen_url`      | VARCHAR(300)  | URL de la imagen de perfil (opcional)     |
| `sitio_web`       | VARCHAR(300)  | Sitio web oficial (opcional)              |
| `estado`          | VARCHAR(255)  | Estado del artista (ej. ACTIVO/INACTIVO)  |
| `creado_en`       | DATETIME      | Fecha de creación                         |
| `actualizado_en`  | DATETIME      | Fecha de última actualización             |

**`agenda_artistas`** (FK → `artistas.id`)

| Campo                 | Tipo          | Descripción                          |
|------------------------|---------------|----------------------------------------|
| `id`                   | BIGINT (PK)   | Identificador autoincremental         |
| `artista_id`           | BIGINT (FK)   | Artista asociado                      |
| `evento_id`            | BIGINT        | Evento relacionado (ms-eventos)       |
| `nombre_evento`        | VARCHAR(255)  | Nombre del evento                     |
| `fecha_presentacion`   | DATETIME      | Fecha/hora de la presentación         |
| `lugar`                | VARCHAR(255)  | Lugar de la presentación              |
| `estado_agenda`        | VARCHAR(255)  | Estado (ej. PENDIENTE/CONFIRMADA/CANCELADA) |
| `notas`                | TEXT          | Notas adicionales (opcional)          |
| `creado_en`            | DATETIME      | Fecha de creación                     |

Las migraciones se gestionan con **Flyway** y están versionadas en `src/main/resources/db/migration`.

---

## 🔌 API REST

Base path: `/api/artistas`

| Método | Endpoint                                  | Descripción                                  |
|--------|--------------------------------------------|-----------------------------------------------|
| GET    | `/api/artistas`                            | Lista todos los artistas                      |
| GET    | `/api/artistas/activos`                    | Lista solo artistas activos                   |
| GET    | `/api/artistas/{id}`                       | Obtiene un artista por ID                     |
| GET    | `/api/artistas/genero/{genero}`            | Lista artistas filtrados por género musical   |
| POST   | `/api/artistas`                            | Crea un nuevo artista                         |
| PUT    | `/api/artistas/{id}`                       | Actualiza los datos de un artista             |
| DELETE | `/api/artistas/{id}`                       | Elimina un artista                            |
| GET    | `/api/artistas/{id}/agenda`                | Lista la agenda de presentaciones del artista |
| POST   | `/api/artistas/{id}/agenda`                | Agrega una fecha a la agenda del artista      |
| PATCH  | `/api/artistas/agenda/{agendaId}/confirmar`| Confirma una fecha agendada                   |
| PATCH  | `/api/artistas/agenda/{agendaId}/cancelar` | Cancela una fecha agendada                    |

### Ejemplo — Crear artista

```http
POST /api/artistas
Content-Type: application/json

{
  "nombre": "Soda Stereo",
  "genero": "Rock",
  "nacionalidad": "Argentina",
  "biografia": "Banda de rock formada en 1982...",
  "imagenUrl": "https://ejemplo.com/imagen.jpg",
  "sitioWeb": "https://sodastereo.com"
}
```

### Ejemplo — Agregar fecha a la agenda

```http
POST /api/artistas/1/agenda
Content-Type: application/json

{
  "eventoId": 10,
  "nombreEvento": "Gira Aniversario",
  "fechaPresentacion": "2026-12-15T21:00:00",
  "lugar": "Movistar Arena, Santiago",
  "notas": "Show acústico"
}
```

### Endpoints de observabilidad (Actuator)

| Endpoint                  | Descripción                            |
|-----------------------------|----------------------------------------|
| `GET /actuator/health`      | Estado de salud del servicio           |
| `GET /actuator/metrics`     | Métricas disponibles                   |
| `GET /actuator/prometheus`  | Métricas en formato Prometheus         |

---

## ⚙️ Configuración

El servicio se configura por variables de entorno (con valores por defecto para desarrollo local):

| Variable             | Descripción                              | Default                  |
|-----------------------|--------------------------------------------|---------------------------|
| `PORT`                | Puerto HTTP del servicio                  | `8086`                    |
| `DB_HOST`             | Host de MySQL                             | `localhost`                |
| `DB_PORT`             | Puerto de MySQL                           | `3306`                     |
| `DB_USERNAME`         | Usuario de MySQL                          | `root`                     |
| `DB_PASSWORD`         | Contraseña de MySQL                       | `root`                     |
| `MS_EVENTOS_URL`      | URL base de `ms-eventos`                  | `http://localhost:8081`    |
| `SPRING_PROFILES_ACTIVE` | Perfil activo (`local`, `docker`, `prod`) controla formato de logs | `default` |

---

## 🚀 Cómo Ejecutar

### Opción A — Local con Maven

```bash
cd ms-artistas/artistas
./mvnw spring-boot:run
```

> Requiere una instancia de MySQL accesible en `localhost:3306` (o ajustar las variables `DB_*`).

### Opción B — Docker (solo este microservicio)

```bash
cd ms-artistas/artistas
docker build -t ms-artistas .
docker run -p 8086:8086 \
  -e DB_HOST=host.docker.internal \
  -e SPRING_PROFILES_ACTIVE=docker \
  ms-artistas
```

### Opción C — Stack completo (todos los microservicios + observabilidad)

```bash
docker compose up -d --build
```

Esto levanta MySQL, todos los microservicios, el API Gateway, Prometheus, Grafana, Loki y Promtail.

| Servicio        | URL                              |
|-------------------|-----------------------------------|
| ms-artistas        | http://localhost:8086             |
| Prometheus         | http://localhost:9090             |
| Grafana            | http://localhost:3000 (admin/admin) |
| Loki               | http://localhost:3100             |

---

## 🧪 Testing y Cobertura

```bash
cd ms-artistas/artistas
./mvnw test                # Ejecuta los tests unitarios
./mvnw test jacoco:report  # Ejecuta tests y genera reporte de cobertura
```

El reporte HTML de cobertura queda disponible en:

```
target/site/jacoco/index.html
```

El build falla automáticamente si la cobertura de instrucciones cae por debajo del **60%** (umbral configurado en el `jacoco-maven-plugin`, ver [pom.xml](./pom.xml)).

---

## 🔄 CI/CD

El pipeline de GitHub Actions ([`.github/workflows/ms-artistas-ci.yml`](https://github.com/DevVivas/ticket-system-ms-artistas/blob/main/.github/workflows/ms-artistas-ci.yml)) se dispara automáticamente ante cambios en `ms-artistas/**` y ejecuta las siguientes etapas en orden:

```
 Build  →  Test + Cobertura (JaCoCo)  →  Quality Gate (SonarCloud)  →  Docker Build & Push
```

1. **Build**: compila el código fuente con Maven.
2. **Test + Cobertura**: corre los tests unitarios, publica resultados JUnit y sube el reporte JaCoCo como artefacto; comenta el % de cobertura en cada Pull Request.
3. **Quality Gate**: ejecuta el análisis estático de SonarCloud y **bloquea el pipeline** si no se aprueba el Quality Gate (bugs, vulnerabilidades, code smells, duplicación, cobertura).
4. **Docker Build & Push**: solo en `main` y tras pasar el Quality Gate, construye la imagen Docker y la publica (si hay credenciales configuradas).

### Secrets / Variables requeridos en GitHub

| Nombre                  | Tipo     | Descripción                              |
|---------------------------|----------|--------------------------------------------|
| `SONAR_TOKEN`              | Secret   | Token de autenticación de SonarCloud       |
| `SONAR_ORGANIZATION`       | Variable | Organización en SonarCloud                 |
| `SONAR_PROJECT_KEY`        | Variable | Clave del proyecto (opcional)              |
| `DOCKERHUB_USERNAME`       | Variable | Usuario de Docker Hub (opcional)           |
| `DOCKERHUB_TOKEN`          | Secret   | Token de Docker Hub (opcional)             |

---

## ✅ Calidad de Código

- **SonarCloud**: análisis estático continuo (bugs, vulnerabilidades, code smells, duplicación, seguridad). Configuración en [`sonar-project.properties`](./sonar-project.properties).
- **JaCoCo**: cobertura de tests con Quality Gate local (mínimo 60% de instrucciones cubiertas), reporte HTML/XML generado en cada build.
- Exclusiones de análisis/cobertura sobre `Model`, `DTO` y la clase de arranque, ya que no contienen lógica de negocio relevante para medir.

---

## 📊 Observabilidad

### Métricas (Prometheus + Grafana)

El servicio expone métricas en `/actuator/prometheus` (JVM, HTTP, HikariCP, GC, CPU) gracias a **Micrometer**. Prometheus las recolecta periódicamente y Grafana las visualiza mediante el dashboard incluido:

📈 [`grafana/ms-artistas-dashboard.json`](./grafana/ms-artistas-dashboard.json)

Paneles incluidos: estado del servicio, uptime, tasa de requests, tasa de errores 5xx, latencia P95/P99 por endpoint, uso de memoria heap, CPU, pool de conexiones HikariCP, hilos JVM, pausas de GC y logs en vivo desde Loki.

Si usas `docker compose up`, el dashboard se **carga automáticamente** en Grafana (carpeta "Ticket System") gracias al aprovisionamiento configurado en `grafana/provisioning/`.

### Logs (Loki + Promtail)

- El servicio emite logs en **formato JSON estructurado** (Logback + `logstash-logback-encoder`) cuando corre con el perfil `docker`/`prod` — ver [`logback-spring.xml`](./src/main/resources/logback-spring.xml).
- **Promtail** descubre automáticamente el contenedor `ms-artistas` y envía sus logs a **Loki**.
- En Grafana, los logs pueden consultarse con el datasource **Loki** usando, por ejemplo:

```logql
{service="ms-artistas"} | json | level="ERROR"
```

---

## 📁 Estructura del Proyecto

```
ms-artistas/artistas/
├── src/
│   ├── main/
│   │   ├── java/com/ticket_system/
│   │   │   ├── Controller/      # Endpoints REST
│   │   │   ├── Service/         # Lógica de negocio
│   │   │   ├── Repository/      # Acceso a datos (Spring Data JPA)
│   │   │   ├── Model/           # Entidades JPA
│   │   │   ├── DTO/             # Objetos de transferencia + validaciones
│   │   │   ├── Exception/       # Manejo centralizado de excepciones
│   │   │   └── ArtistasApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── logback-spring.xml
│   │       └── db/migration/    # Scripts Flyway
│   └── test/                    # Tests unitarios
├── grafana/
│   └── ms-artistas-dashboard.json
├── Dockerfile
├── pom.xml
└── sonar-project.properties
```

---

## 👥 Equipo

* Abrahan Vivas
* Renato Uribe

---

## 📄 Licencia

Proyecto desarrollado con fines educativos como parte de la formación en **Ingeniería en Informática**.
