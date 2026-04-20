# QuarkStack 🔥

> **Full-Stack con Quarkus + React — de cero a la nube**
> Curso completo, gratuito, en español.

[![YouTube](https://img.shields.io/badge/YouTube-Serie%20Completa-red?style=flat&logo=youtube)](https://www.youtube.com/playlist?list=PLN_Z6yes3V5A2y16thmIah2Bm5bnfzkLZ)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.x-4695EB?logo=quarkus)](https://quarkus.io)
[![React](https://img.shields.io/badge/React-19-61DAFB?logo=react)](https://react.dev)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)
[![Node](https://img.shields.io/badge/Node-24%20LTS-339933?logo=nodedotjs)](https://nodejs.org)

---

## 📖 ¿Qué es esto?

**QuarkStack** es el proyecto del curso homónimo: un sistema de gestión de tareas colaborativo en tiempo real (estilo Jira simplificado), construido con Quarkus y React, desplegado en Azure.

El curso cubre desde los fundamentos hasta producción, incluyendo:

- Arquitectura Hexagonal real con **Maven multi-módulo** (el compilador hace cumplir las reglas)
- CQRS con PostgreSQL para escrituras y MongoDB para lecturas
- Event-Driven Architecture con Apache Kafka
- Programación reactiva con Mutiny y Server-Sent Events
- Seguridad con OIDC y Keycloak
- Observabilidad con OpenTelemetry + Grafana (métricas, trazas y logs)
- Frontend en React 19 desde cero para Java devs
- Testing con JUnit 5, Mockito y Testcontainers
- Despliegue en Azure Container Apps con GitHub Actions

---

## 🗂️ Índice de Episodios

| #                                          | Episodio                                      | Tag     |
|--------------------------------------------|-----------------------------------------------|---------|
| [00](docs/ep00-bienvenida/README.md)       | Bienvenida y arquitectura del proyecto        | `ep-00` |
| [01](docs/ep01-setup/README.md)            | Setup del entorno completo                    | `ep-01` |
| [02](docs/ep02-hexagonal-teoria/README.md) | Arquitectura Hexagonal: puertos y adaptadores | `ep-02` |
| [03](docs/ep03-dominio/README.md)          | El dominio: entidades, VOs y casos de uso     | `ep-03` |
| ...                                        | ...                                           | ...     |

> Consulta el [Episodio 00](docs/ep00-bienvenida/README.md) para el índice completo del curso.

---

## 🚀 Inicio rápido

> Asegúrate de tener instalados: Java 21, Node 24 LTS, Docker Desktop y Git.
> El setup detallado está en el [Episodio 01](docs/ep01-setup/README.md).

🐧 Bash:
```bash
git clone git@github.com:apuntesdejava/fullstack-quarkus-react-tutorial.git
cd fullstack-quarkus-react-tutorial

# Infraestructura local
docker compose -f infra/docker/docker-compose.yml up -d

# Backend (desde el módulo runner)
cd backend
./mvnw quarkus:dev -pl quarkstack-runner

# Frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

🪟 PowerShell:
```powershell
git clone git@github.com:apuntesdejava/fullstack-quarkus-react-tutorial.git
cd fullstack-quarkus-react-tutorial

# Infraestructura local
docker compose -f infra/docker/docker-compose.yml up -d

# Backend (desde el módulo runner)
cd backend
.\mvnw.cmd quarkus:dev -pl quarkstack-runner

# Frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

| Servicio       | URL                              |
|----------------|----------------------------------|
| Frontend React | http://localhost:5173            |
| Backend API    | http://localhost:8080            |
| Swagger UI     | http://localhost:8080/swagger-ui |
| Quarkus Dev UI | http://localhost:8080/q/dev      |
| Kafka UI       | http://localhost:8090            |

---

## 🏗️ Stack Tecnológico

**Backend:** Quarkus 3.x · Jakarta EE · MicroProfile · Mutiny · Panache · Flyway · OpenTelemetry

**Frontend:** React 19 · Vite · Tailwind CSS · React Router · TanStack Query

**Infraestructura:** PostgreSQL · MongoDB · Apache Kafka · Keycloak · Docker

**Cloud:** Azure Container Apps · Azure Static Web Apps · Azure Event Hubs · Azure Cosmos DB

---

## 🗂️ Estructura del Repositorio

```
fullstack-quarkus-react-tutorial/
│
├── backend/                        ← Proyecto Maven multi-módulo
│   ├── pom.xml                     ← Parent POM
│   ├── quarkstack-domain/          ← Java puro. Sin frameworks.
│   ├── quarkstack-application/     ← Casos de uso. Solo conoce al dominio.
│   ├── quarkstack-adapter-rest/    ← JAX-RS + OpenAPI (Quarkus)
│   ├── quarkstack-adapter-persistence/ ← Panache PG + Mongo + Flyway
│   ├── quarkstack-adapter-messaging/   ← SmallRye + Kafka
│   └── quarkstack-runner/          ← Ensambla todo. Ejecuta Quarkus.
│
├── frontend/                       ← React 19 + Vite + Tailwind CSS
│
├── infra/
│   ├── docker/                     ← Docker Compose local
│   └── azure/                      ← Scripts Az CLI para despliegue
│
└── docs/                           ← Tutoriales por episodio
```

> Árbol completo con todos los archivos y el episodio donde se crean: [docs/ESTRUCTURA.md](docs/ESTRUCTURA.md)

---

## 📋 Prerrequisitos

- Java 21
- Node 24 LTS
- Docker Desktop
- Git

---

## 📄 Licencia

Apache License 2.0 — ver [LICENSE](LICENSE)

---

*QuarkStack — Construido con ❤️ y mucho ☕*