# Estructura del Repositorio QuarkBoard

Referencia completa del árbol de archivos.
Usa esta guía para saber exactamente dónde colocar cada archivo del curso.

---

```
quarkboard/                                   ← Raíz del repositorio
│
├── README.md                                 ✅ ep00  Índice principal del repo
│
├── .github/
│   ├── workflows/
│   │   ├── build.yml                         🔜 ep31  CI: build y tests
│   │   └── deploy.yml                        🔜 ep31  CD: push a Azure Container Registry
│   └── DISCUSSION_TEMPLATE/
│       └── general.yml                       🔜 ep00  Template de discusiones
│
├── docs/                                     ← Tutoriales por episodio (Markdown)
│   │
│   ├── ep00-bienvenida/
│   │   └── README.md                         ✅ ep00  Bienvenida y arquitectura
│   │
│   ├── ep01-setup/
│   │   └── README.md                         🔜 ep01  Setup del entorno
│   │
│   ├── ep02-hexagonal-teoria/
│   │   └── README.md                         🔜 ep02  Arquitectura Hexagonal
│   │
│   ├── ep03-dominio/
│   │   └── README.md                         🔜 ep03  Entidades y casos de uso
│   │
│   ├── ep04-rest-adapter/
│   │   └── README.md                         🔜 ep04  JAX-RS + OpenAPI
│   │
│   ├── ep05-persistencia-pg/
│   │   └── README.md                         🔜 ep05  PostgreSQL + Panache
│   │
│   ├── ep06-react-intro/
│   │   └── README.md                         🔜 ep06  React para Java devs
│   │
│   ├── ep07-estado-efectos/
│   │   └── README.md                         🔜 ep07  useState + useEffect
│   │
│   ├── ep08-routing-layout/
│   │   └── README.md                         🔜 ep08  React Router + Tailwind
│   │
│   ├── ep09-consumo-api/
│   │   └── README.md                         🔜 ep09  Consumo REST desde React
│   │
│   ├── ep10-kanban-board/
│   │   └── README.md                         🔜 ep10  Componente Kanban
│   │
│   ├── ep11-jpa-flyway/
│   │   └── README.md                         🔜 ep11  Relaciones JPA + Flyway
│   │
│   ├── ep12-mongodb/
│   │   └── README.md                         🔜 ep12  MongoDB + Panache
│   │
│   ├── ep13-busqueda-paginacion/
│   │   └── README.md                         🔜 ep13  Filtros y paginación
│   │
│   ├── ep14-cqrs-teoria/
│   │   └── README.md                         🔜 ep14  CQRS: teoría y diseño
│   │
│   ├── ep15-commands/
│   │   └── README.md                         🔜 ep15  Bus de comandos con CDI
│   │
│   ├── ep16-queries/
│   │   └── README.md                         🔜 ep16  Proyecciones en MongoDB
│   │
│   ├── ep17-kafka-setup/
│   │   └── README.md                         🔜 ep17  Kafka con Docker
│   │
│   ├── ep18-kafka-producer/
│   │   └── README.md                         🔜 ep18  Reactive Messaging: producers
│   │
│   ├── ep19-kafka-consumer/
│   │   └── README.md                         🔜 ep19  Reactive Messaging: consumers
│   │
│   ├── ep20-eda-resilience/
│   │   └── README.md                         🔜 ep20  DLQ, retry y fallback
│   │
│   ├── ep21-mutiny/
│   │   └── README.md                         🔜 ep21  Uni y Multi con Mutiny
│   │
│   ├── ep22-rest-reactivo/
│   │   └── README.md                         🔜 ep22  RESTEasy Reactive
│   │
│   ├── ep23-sse/
│   │   └── README.md                         🔜 ep23  Server-Sent Events
│   │
│   ├── ep24-oidc-keycloak/
│   │   └── README.md                         🔜 ep24  OIDC + Keycloak
│   │
│   ├── ep25-react-auth/
│   │   └── README.md                         🔜 ep25  Login y rutas protegidas
│   │
│   ├── ep26-unit-testing/
│   │   └── README.md                         🔜 ep26  JUnit 5 + Mockito
│   │
│   ├── ep27-integration-testing/
│   │   └── README.md                         🔜 ep27  @QuarkusTest + Testcontainers
│   │
│   ├── ep28-react-testing/
│   │   └── README.md                         🔜 ep28  Vitest + Testing Library
│   │
│   ├── ep28b-o11y/
│   │   └── README.md                         🔜 ep28b OpenTelemetry + Grafana
│   │
│   ├── ep29-dockerfile/
│   │   └── README.md                         🔜 ep29  Dockerfile optimizado
│   │
│   ├── ep30-compose-completo/
│   │   └── README.md                         🔜 ep30  Docker Compose full stack
│   │
│   ├── ep31-cicd/
│   │   └── README.md                         🔜 ep31  GitHub Actions + ACR
│   │
│   ├── ep32-azure-infra/
│   │   └── README.md                         🔜 ep32  Scripts Az CLI
│   │
│   ├── ep33-azure-deploy/
│   │   └── README.md                         🔜 ep33  Deploy en Azure
│   │
│   ├── ep34-azure-o11y/
│   │   └── README.md                         🔜 ep34  Azure Monitor + App Insights
│   │
│   ├── epB1-native/
│   │   └── README.md                         🔜 bonus Quarkus Native + GraalVM
│   │
│   ├── epB2-terraform/
│   │   └── README.md                         🔜 bonus IaC con Terraform
│   │
│   └── epB3-websockets/
│       └── README.md                         🔜 bonus WebSockets
│
├── backend/                                  ← Aplicación Quarkus
│   ├── pom.xml                               🔜 ep01
│   └── src/
│       ├── main/
│       │   ├── java/dev/quarkboard/
│       │   │   ├── domain/
│       │   │   │   ├── model/                🔜 ep03  Task, Project, User, VOs
│       │   │   │   ├── port/
│       │   │   │   │   ├── in/               🔜 ep03  Puertos de entrada (use cases)
│       │   │   │   │   └── out/              🔜 ep03  Puertos de salida (repos, events)
│       │   │   │   └── usecase/              🔜 ep03  Implementación de casos de uso
│       │   │   ├── adapter/
│       │   │   │   ├── rest/                 🔜 ep04  JAX-RS resources
│       │   │   │   ├── persistence/
│       │   │   │   │   ├── pg/               🔜 ep05  Panache PostgreSQL
│       │   │   │   │   └── mongo/            🔜 ep12  Panache MongoDB
│       │   │   │   └── messaging/
│       │   │   │       ├── producer/         🔜 ep18  Kafka producers
│       │   │   │       └── consumer/         🔜 ep19  Kafka consumers
│       │   │   └── infrastructure/
│       │   │       ├── config/               🔜 ep02  Configuración CDI
│       │   │       ├── exception/            🔜 ep04  Exception mappers
│       │   │       └── mapper/               🔜 ep04  DTO ↔ Domain mappers
│       │   └── resources/
│       │       ├── application.properties    🔜 ep01
│       │       └── db/migration/
│       │           ├── V1__create_tables.sql 🔜 ep05  Flyway: tablas iniciales
│       │           └── V2__add_indexes.sql   🔜 ep11  Flyway: índices y relaciones
│       └── test/
│           ├── java/dev/quarkboard/
│           │   ├── domain/                   🔜 ep26  Tests unitarios del dominio
│           │   └── adapter/
│           │       ├── rest/                 🔜 ep27  @QuarkusTest REST
│           │       └── persistence/          🔜 ep27  Testcontainers
│           └── resources/
│               └── application.properties   🔜 ep27  Config de test
│
├── frontend/                                 ← Aplicación React
│   ├── package.json                          🔜 ep06
│   ├── vite.config.js                        🔜 ep06
│   ├── tailwind.config.js                    🔜 ep08
│   ├── index.html                            🔜 ep06
│   └── src/
│       ├── main.jsx                          🔜 ep06
│       ├── App.jsx                           🔜 ep08
│       ├── components/
│       │   ├── KanbanBoard.jsx               🔜 ep10
│       │   ├── TaskCard.jsx                  🔜 ep10
│       │   └── Navbar.jsx                    🔜 ep08
│       ├── pages/
│       │   ├── HomePage.jsx                  🔜 ep08
│       │   ├── ProjectPage.jsx               🔜 ep10
│       │   └── LoginPage.jsx                 🔜 ep25
│       ├── hooks/
│       │   ├── useTasks.js                   🔜 ep09
│       │   └── useSSE.js                     🔜 ep23
│       ├── services/
│       │   ├── api.js                        🔜 ep09  Cliente base
│       │   ├── taskService.js                🔜 ep09
│       │   └── projectService.js             🔜 ep09
│       └── context/
│           └── AuthContext.jsx               🔜 ep25
│
└── infra/
    ├── docker/
    │   ├── docker-compose.yml                🔜 ep01  Stack base (PG, Mongo, Kafka, KC)
    │   └── docker-compose.o11y.yml           🔜 ep28b Stack observabilidad (OTel, Grafana)
    │       └── otel/
    │           └── collector.yml             🔜 ep28b Config del OTel Collector
    ├── azure/
    │   ├── 01-create-infra.sh                🔜 ep32  Crea recursos Azure
    │   ├── 02-deploy-apps.sh                 🔜 ep33  Despliega contenedores
    │   ├── 03-configure-secrets.sh           🔜 ep33  Configura Key Vault
    │   └── 99-destroy.sh                     🔜 ep32  ⚠️ Borra TODO
    └── terraform/                            🔜 bonus
        ├── main.tf
        ├── variables.tf
        └── modules/
            ├── container-apps/
            ├── databases/
            └── messaging/
```

---

## Leyenda

| Símbolo | Significado |
|---|---|
| ✅ | Archivo ya creado en este episodio |
| 🔜 | Archivo que se crea en el episodio indicado |
| `epXX` | Número de episodio donde aparece ese archivo |
| `bonus` | Episodio bonus |

---

## Ramas del repositorio

Cada episodio tiene su propia rama. El flujo es:

```
main                    ← Código final completo del curso
│
├── ep/00-bienvenida    ← Solo README y docs/ep00
├── ep/01-setup         ← Agrega: pom.xml, docker-compose base, frontend scaffold
├── ep/02-hexagonal     ← Agrega: estructura de paquetes backend
├── ep/03-dominio       ← Agrega: domain/model, domain/port, domain/usecase
│   ...
└── ep/34-azure-o11y    ← Código completo
```

La regla: **cada rama acumula todo lo anterior más lo nuevo del episodio**. El alumno puede hacer `git checkout ep/XX-nombre` para ver el estado exacto del proyecto en ese punto del curso.
