# Estructura del Repositorio QuarkStack

Referencia completa del árbol de archivos.
Usa esta guía para saber exactamente dónde colocar cada archivo del curso.

---

```
fullstack-quarkus-react-tutorial/          ← Raíz del repositorio
│
├── README.md                              ✅ ep00  Índice principal del repo
│
├── .github/
│   ├── workflows/
│   │   ├── build.yml                      🔜 ep31  CI: build y tests
│   │   └── deploy.yml                     🔜 ep31  CD: push a Azure Container Registry
│   └── DISCUSSION_TEMPLATE/
│       └── general.yml                    🔜 ep00  Template de discusiones
│
├── docs/                                  ← Tutoriales por episodio (Markdown)
│   ├── ESTRUCTURA.md                      ✅ ep00  Este archivo
│   ├── ep00-bienvenida/
│   │   └── README.md                      ✅ ep00  Bienvenida y arquitectura
│   ├── ep01-setup/
│   │   └── README.md                      ✅ ep01  Setup del entorno
│   ├── ep02-hexagonal-teoria/
│   │   └── README.md                      🔜 ep02  Arquitectura Hexagonal
│   ├── ep03-dominio/
│   │   └── README.md                      🔜 ep03  Entidades y casos de uso
│   ├── ep04-rest-adapter/
│   │   └── README.md                      🔜 ep04  JAX-RS + OpenAPI
│   ├── ep05-persistencia-pg/
│   │   └── README.md                      🔜 ep05  PostgreSQL + Panache
│   ├── ep06-react-intro/
│   │   └── README.md                      🔜 ep06  React para Java devs
│   ├── ep07-estado-efectos/
│   │   └── README.md                      🔜 ep07  useState + useEffect
│   ├── ep08-routing-layout/
│   │   └── README.md                      🔜 ep08  React Router + Tailwind
│   ├── ep09-consumo-api/
│   │   └── README.md                      🔜 ep09  Consumo REST desde React
│   ├── ep10-kanban-board/
│   │   └── README.md                      🔜 ep10  Componente Kanban
│   ├── ep11-jpa-flyway/
│   │   └── README.md                      🔜 ep11  Relaciones JPA + Flyway
│   ├── ep12-mongodb/
│   │   └── README.md                      🔜 ep12  MongoDB + Panache
│   ├── ep13-busqueda-paginacion/
│   │   └── README.md                      🔜 ep13  Filtros y paginación
│   ├── ep14-cqrs-teoria/
│   │   └── README.md                      🔜 ep14  CQRS: teoría y diseño
│   ├── ep15-commands/
│   │   └── README.md                      🔜 ep15  Bus de comandos con CDI
│   ├── ep16-queries/
│   │   └── README.md                      🔜 ep16  Proyecciones en MongoDB
│   ├── ep17-kafka-setup/
│   │   └── README.md                      🔜 ep17  Kafka con Docker
│   ├── ep18-kafka-producer/
│   │   └── README.md                      🔜 ep18  Reactive Messaging: producers
│   ├── ep19-kafka-consumer/
│   │   └── README.md                      🔜 ep19  Reactive Messaging: consumers
│   ├── ep20-eda-resilience/
│   │   └── README.md                      🔜 ep20  DLQ, retry y fallback
│   ├── ep21-mutiny/
│   │   └── README.md                      🔜 ep21  Uni y Multi con Mutiny
│   ├── ep22-rest-reactivo/
│   │   └── README.md                      🔜 ep22  RESTEasy Reactive
│   ├── ep23-sse/
│   │   └── README.md                      🔜 ep23  Server-Sent Events
│   ├── ep24-oidc-keycloak/
│   │   └── README.md                      🔜 ep24  OIDC + Keycloak
│   ├── ep25-react-auth/
│   │   └── README.md                      🔜 ep25  Login y rutas protegidas
│   ├── ep26-unit-testing/
│   │   └── README.md                      🔜 ep26  JUnit 5 + Mockito
│   ├── ep27-integration-testing/
│   │   └── README.md                      🔜 ep27  @QuarkusTest + Testcontainers
│   ├── ep28-react-testing/
│   │   └── README.md                      🔜 ep28  Vitest + Testing Library
│   ├── ep28b-o11y/
│   │   └── README.md                      🔜 ep28b OpenTelemetry + Grafana
│   ├── ep29-dockerfile/
│   │   └── README.md                      🔜 ep29  Dockerfile optimizado
│   ├── ep30-compose-completo/
│   │   └── README.md                      🔜 ep30  Docker Compose full stack
│   ├── ep31-cicd/
│   │   └── README.md                      🔜 ep31  GitHub Actions + ACR
│   ├── ep32-azure-infra/
│   │   └── README.md                      🔜 ep32  Scripts Az CLI
│   ├── ep33-azure-deploy/
│   │   └── README.md                      🔜 ep33  Deploy en Azure
│   ├── ep34-azure-o11y/
│   │   └── README.md                      🔜 ep34  Azure Monitor + App Insights
│   ├── epB1-native/
│   │   └── README.md                      🔜 bonus Quarkus Native + GraalVM
│   ├── epB2-terraform/
│   │   └── README.md                      🔜 bonus IaC con Terraform
│   └── epB3-websockets/
│       └── README.md                      🔜 bonus WebSockets
│
├── backend/                               ← Proyecto Maven multi-módulo
│   │
│   ├── pom.xml                            ✅ ep01  Parent POM (gestión centralizada)
│   ├── mvnw                               ✅ ep01  Maven Wrapper (🐧)
│   ├── mvnw.cmd                           ✅ ep01  Maven Wrapper (🪟)
│   │
│   ├── quarkstack-domain/                 ← Java puro. CERO dependencias de frameworks
│   │   ├── pom.xml                        ✅ ep01
│   │   └── src/
│   │       ├── main/java/dev/quarkstack/domain/
│   │       │   ├── model/                 🔜 ep03  Task, Project, User, Value Objects
│   │       │   └── port/
│   │       │       ├── in/                🔜 ep03  Interfaces de casos de uso (entrada)
│   │       │       └── out/               🔜 ep03  Interfaces de repos y eventos (salida)
│   │       └── test/java/dev/quarkstack/domain/
│   │           └── (tests unitarios puros) 🔜 ep26
│   │
│   ├── quarkstack-application/            ← Casos de uso. Solo conoce a domain
│   │   ├── pom.xml                        ✅ ep01
│   │   └── src/
│   │       ├── main/java/dev/quarkstack/application/
│   │       │   ├── usecase/               🔜 ep03  Implementación de casos de uso
│   │       │   └── service/               🔜 ep03  Servicios de aplicación
│   │       └── test/java/dev/quarkstack/application/
│   │           └── (tests con Mockito)    🔜 ep26
│   │
│   ├── quarkstack-adapter-rest/           ← Quarkus REST. Conoce a application
│   │   ├── pom.xml                        ✅ ep01
│   │   └── src/main/java/dev/quarkstack/adapter/rest/
│   │       └── (JAX-RS resources + DTOs)  🔜 ep04
│   │
│   ├── quarkstack-adapter-persistence/    ← Panache PG + Mongo + Flyway
│   │   ├── pom.xml                        ✅ ep01
│   │   └── src/
│   │       ├── main/java/dev/quarkstack/adapter/persistence/
│   │       │   ├── pg/                    🔜 ep05  Repositorios PostgreSQL
│   │       │   └── mongo/                 🔜 ep12  Repositorios MongoDB
│   │       └── main/resources/
│   │           └── db/migration/
│   │               ├── V1__create_tables.sql  🔜 ep05  Flyway: tablas iniciales
│   │               └── V2__add_indexes.sql    🔜 ep11  Flyway: índices y relaciones
│   │
│   ├── quarkstack-adapter-messaging/      ← SmallRye Reactive Messaging + Kafka
│   │   ├── pom.xml                        ✅ ep01
│   │   └── src/main/java/dev/quarkstack/adapter/messaging/
│   │       ├── producer/                  🔜 ep18  Kafka producers
│   │       └── consumer/                  🔜 ep19  Kafka consumers
│   │
│   └── quarkstack-runner/                 ← Ensambla todo. Único con quarkus-maven-plugin
│       ├── pom.xml                        ✅ ep01
│       └── src/
│           ├── main/java/dev/quarkstack/infrastructure/
│           │   ├── config/                🔜 ep02  Producers CDI, configuración
│           │   ├── exception/             🔜 ep04  ExceptionMappers JAX-RS
│           │   └── mapper/                🔜 ep04  DTO ↔ Domain mappers
│           ├── main/resources/
│           │   └── application.properties ✅ ep01
│           └── test/java/dev/quarkstack/
│               └── (tests @QuarkusTest)   🔜 ep27
│
├── frontend/                              ← Aplicación React 19 + Vite
│   ├── package.json                       ✅ ep01
│   ├── vite.config.js                     ✅ ep01
│   ├── index.html                         ✅ ep01
│   └── src/
│       ├── main.jsx                       ✅ ep01
│       ├── App.jsx                        ✅ ep01  (pantalla inicial)
│       ├── index.css                      ✅ ep01  (Tailwind import)
│       ├── components/
│       │   ├── KanbanBoard.jsx            🔜 ep10
│       │   ├── TaskCard.jsx               🔜 ep10
│       │   └── Navbar.jsx                 🔜 ep08
│       ├── pages/
│       │   ├── HomePage.jsx               🔜 ep08
│       │   ├── ProjectPage.jsx            🔜 ep10
│       │   └── LoginPage.jsx              🔜 ep25
│       ├── hooks/
│       │   ├── useTasks.js                🔜 ep09
│       │   └── useSSE.js                  🔜 ep23
│       ├── services/
│       │   ├── api.js                     🔜 ep09  Cliente base Axios
│       │   ├── taskService.js             🔜 ep09
│       │   └── projectService.js          🔜 ep09
│       └── context/
│           └── AuthContext.jsx            🔜 ep25
│
└── infra/
    ├── docker/
    │   ├── docker-compose.yml             ✅ ep01  Stack local: PG, Mongo, Kafka, KC
    │   ├── docker-compose.o11y.yml        🔜 ep28b Stack observabilidad
    │   └── otel/
    │       └── collector.yml              🔜 ep28b Config del OTel Collector
    ├── azure/
    │   ├── 01-create-infra.sh             🔜 ep32  Crea recursos Azure
    │   ├── 02-deploy-apps.sh              🔜 ep33  Despliega contenedores
    │   ├── 03-configure-secrets.sh        🔜 ep33  Configura Key Vault
    │   └── 99-destroy.sh                  🔜 ep32  ⚠️ Borra TODO
    └── terraform/                         🔜 bonus
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
| ✅ | Archivo ya creado |
| 🔜 | Se crea en el episodio indicado |
| `ep01` | Número del episodio donde aparece el archivo |
| `bonus` | Episodio bonus |

---

## Dependencias entre módulos del backend

```
quarkstack-domain          ← Sin dependencias externas. Java puro.
       ▲
quarkstack-application     ← Solo conoce a domain
       ▲
┌──────┴──────────────────────────┐
▲                ▲                ▲
quarkstack-      quarkstack-      quarkstack-
adapter-rest     adapter-         adapter-
                 persistence      messaging
▲                ▲                ▲
└──────┬──────────────────────────┘
quarkstack-runner          ← Ensambla todo. Tiene quarkus-maven-plugin.
```

**La regla:** las flechas solo apuntan hacia adentro (hacia domain).
Los adaptadores no se conocen entre sí. El runner no tiene lógica de negocio.

---

## Comandos esenciales desde `backend/`

```bash
# Compilar todos los módulos
./mvnw clean compile                           # 🐧
.\mvnw.cmd clean compile                       # 🪟

# Arrancar en dev mode (desde el runner)
./mvnw quarkus:dev -pl quarkstack-runner       # 🐧
.\mvnw.cmd quarkus:dev -pl quarkstack-runner   # 🪟

# Ejecutar todos los tests
./mvnw test                                    # 🐧
.\mvnw.cmd test                                # 🪟

# Build completo con imagen Docker
./mvnw clean package -Dquarkus.container-image.build=true  # 🐧
```

---

## Ramas y tags del repositorio

```
main                        ← Código acumulado hasta el último episodio publicado
│
├── ep/00-bienvenida  →  tag: ep-00
├── ep/01-setup       →  tag: ep-01
├── ep/02-hexagonal   →  tag: ep-02
│   ...
└── ep/34-azure-o11y  →  tag: ep-34
```

Para ir al estado del proyecto en un episodio concreto:
```bash
git checkout ep-05   # Estado exacto al terminar el episodio 05
git checkout main    # Volver al código más reciente
```