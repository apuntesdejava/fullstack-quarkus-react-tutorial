# QuarkBoard 🔥

> **QuarkStack: Full-Stack con Quarkus + React — de cero a la nube**
> Curso completo, gratuito, en español.

[![YouTube](https://img.shields.io/badge/YouTube-Serie%20Completa-red?style=flat&logo=youtube)](https://youtube.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.x-4695EB?logo=quarkus)](https://quarkus.io)
[![React](https://img.shields.io/badge/React-19-61DAFB?logo=react)](https://react.dev)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)

---

## 📖 ¿Qué es esto?

**QuarkBoard** es el proyecto del curso *QuarkStack*: un sistema de gestión de tareas colaborativo en tiempo real (estilo Jira simplificado), construido con Quarkus y React, desplegado en Azure.

El curso cubre desde los fundamentos hasta producción, incluyendo:

- Arquitectura Hexagonal + CQRS
- Event-Driven Architecture con Apache Kafka
- Programación reactiva con Mutiny
- PostgreSQL (writes) + MongoDB (reads)
- Seguridad con OIDC y Keycloak
- Observabilidad con OpenTelemetry + Grafana
- Frontend en React (desde cero, para Java devs)
- Testing con JUnit 5, Mockito y Testcontainers
- Despliegue en Azure Container Apps con GitHub Actions

---

## 🗂️ Índice de Episodios

| # | Episodio | Rama |
|--------------------------------------|----------------------------------------|--------------------|
| [00](docs/ep00-bienvenida/README.md) | Bienvenida y arquitectura del proyecto | `ep/00-bienvenida` |
| [01](docs/ep01-setup/README.md)      | Setup del entorno completo             | `ep/01-setup` |
| ... | ... | ... |

> Consulta el [Episodio 00](docs/ep00-bienvenida/README.md) para el índice completo y la arquitectura detallada.

---

## 🚀 Inicio rápido

```bash
# Clonar el repositorio
git clone git@github.com:apuntesdejava/fullstack-quarkus-react-tutorial.git
cd fullstack-quarkus-react-tutorial

# Levantar la infraestructura local (PostgreSQL, MongoDB, Kafka, Keycloak)
docker compose -f infra/docker/docker-compose.yml up -d

# Backend
cd backend
./mvnw quarkus:dev

# Frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

> Para instrucciones detalladas por episodio, navega a la carpeta `docs/epXX-nombre/`.

---

## 🏗️ Stack Tecnológico

**Backend:** Quarkus 3.x · Jakarta EE · MicroProfile · Mutiny · Panache · Flyway · OpenTelemetry

**Frontend:** React 19 · Vite · Tailwind CSS · React Router · TanStack Query

**Infraestructura:** PostgreSQL · MongoDB · Apache Kafka · Keycloak · Docker

**Cloud:** Azure Container Apps · Azure Static Web Apps · Azure Event Hubs · Azure Cosmos DB

---

## 📋 Prerrequisitos

- Java 21
- Node.js 24 LTS
- Docker Desktop
- Git

---

## 📄 Licencia

Apache License 2.0 — ver [LICENSE](LICENSE)

---

*QuarkStack — Construido con ❤️ y mucho ☕*
