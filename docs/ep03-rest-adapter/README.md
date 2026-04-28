# Episodio 03 — Adaptador REST: JAX-RS, OpenAPI y Validaciones

> **Rama:** `ep/03-rest-adapter` | **Tag:** `ep-03`

---

## 🎯 Objetivo

Al terminar este episodio tendrás:

- Los primeros endpoints REST funcionando: proyectos y tareas
- DTOs de request/response en el adaptador REST
- Documentación OpenAPI automática vía Swagger UI
- Validaciones con Bean Validation
- Exception mappers para respuestas de error consistentes
- Cableado CDI en el runner que conecta casos de uso con implementaciones en memoria
- La aplicación corriendo end-to-end: HTTP → caso de uso → dominio → respuesta



---

## 📁 Archivos de este episodio

```
backend/
│
├── quarkstack-adapter-rest/src/main/java/dev/quarkstack/adapter/rest/
│   ├── dto/
│   │   ├── CreateProjectRequest.java
│   │   ├── CreateTaskRequest.java
│   │   ├── MoveTaskRequest.java
│   │   ├── ProjectResponse.java
│   │   └── TaskResponse.java
│   └── resource/
│       ├── ProjectResource.java
│       └── TaskResource.java
│
└── quarkstack-runner/src/main/java/dev/quarkstack/infrastructure/
    ├── config/
    │   └── UseCaseProducer.java
    ├── exception/
    │   ├── IllegalArgumentExceptionMapper.java
    │   └── IllegalStateExceptionMapper.java
    └── repository/
        ├── InMemoryProjectRepository.java
        └── InMemoryTaskRepository.java
```

> 💡 **¿Por qué repositorios en memoria?** La persistencia real con PostgreSQL la construimos en el Ep 05. Por ahora usamos implementaciones en memoria para que la aplicación funcione end-to-end desde ya, sin bloquear el avance. Cambiar de implementación en memoria a Panache será tan simple como reemplazar el producer CDI — eso es exactamente la promesa de la arquitectura hexagonal.

---

## El flujo completo de este episodio

```
HTTP POST /api/projects
         │
         ▼
  [ProjectResource]          ← adapter-rest
  Valida DTO con Bean Validation
  Convierte DTO → Command
         │
         ▼
  [CreateProjectUseCase]     ← puerto IN (domain)
  (implementado por)
         │
         ▼
  [CreateProjectService]     ← application
  Crea entidad Project
  Llama a ProjectRepository.save()
         │
         ▼
  [InMemoryProjectRepository] ← runner (temporal, hasta Ep 05)
  Guarda en un ConcurrentHashMap
         │
         ▼
  [ProjectResource]
  Convierte Project → ProjectResponse
  Retorna 201 Created
```

---

## Paso 1 — DTOs del adaptador REST

Los DTOs (Data Transfer Objects) viven en el adaptador REST, no en el dominio. El dominio no sabe que existe HTTP ni JSON.

### `adapter/rest/dto/CreateProjectRequest.java`

```java
package dev.quarkstack.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para crear un proyecto.
 * Las validaciones se aplican antes de llegar al caso de uso.
 */
public record CreateProjectRequest(

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    String name,

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    String description,

    @NotBlank(message = "El propietario es obligatorio")
    String ownerId
) {}
```

### `adapter/rest/dto/CreateTaskRequest.java`

```java
package dev.quarkstack.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(

    @NotBlank(message = "El título de la tarea es obligatorio")
    @Size(max = 300, message = "El título no puede superar los 300 caracteres")
    String title,

    @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
    String description,

    @NotBlank(message = "El usuario creador es obligatorio")
    String createdByUserId
) {}
```

### `adapter/rest/dto/MoveTaskRequest.java`

```java
package dev.quarkstack.adapter.rest.dto;

import dev.quarkstack.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record MoveTaskRequest(

    @NotNull(message = "El nuevo estado es obligatorio")
    TaskStatus newStatus,

    @NotNull(message = "El usuario que mueve la tarea es obligatorio")
    String movedByUserId
) {}
```

### `adapter/rest/dto/ProjectResponse.java`

```java
package dev.quarkstack.adapter.rest.dto;

import dev.quarkstack.domain.model.Project;

import java.time.Instant;

/**
 * DTO de salida para un proyecto.
 * Se construye desde la entidad del dominio — el dominio no sabe de este DTO.
 */
public record ProjectResponse(
    String id,
    String name,
    String description,
    String ownerId,
    Instant createdAt
) {
    /** Factory method: convierte la entidad del dominio a DTO de respuesta */
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
            project.id().toString(),
            project.name(),
            project.description(),
            project.ownerId().toString(),
            project.createdAt()
        );
    }
}
```

### `adapter/rest/dto/TaskResponse.java`

```java
package dev.quarkstack.adapter.rest.dto;

import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskStatus;

import java.time.Instant;

public record TaskResponse(
    String id,
    String projectId,
    String title,
    String description,
    TaskStatus status,
    String assigneeId,
    String createdBy,
    Instant createdAt,
    Instant updatedAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.id().toString(),
            task.projectId().toString(),
            task.title(),
            task.description(),
            task.status(),
            task.assigneeId() != null ? task.assigneeId().toString() : null,
            task.createdBy().toString(),
            task.createdAt(),
            task.updatedAt()
        );
    }
}
```

---

## Paso 2 — JAX-RS Resources

### `adapter/rest/resource/ProjectResource.java`

```java
package dev.quarkstack.adapter.rest.resource;

import dev.quarkstack.adapter.rest.dto.CreateProjectRequest;
import dev.quarkstack.adapter.rest.dto.ProjectResponse;
import dev.quarkstack.domain.port.in.CreateProjectUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Adaptador de entrada REST para proyectos.
 *
 * Este resource solo sabe de HTTP, DTOs y casos de uso.
 * No conoce entidades del dominio ni repositorios.
 */
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Projects", description = "Gestión de proyectos")
public class ProjectResource {

    @Inject
    CreateProjectUseCase createProjectUseCase;

    @POST
    @Operation(summary = "Crear un nuevo proyecto")
    public Response create(@Valid CreateProjectRequest request) {
        var command = new CreateProjectUseCase.Command(
            request.name(),
            request.description(),
            request.ownerId()
        );

        var project = createProjectUseCase.execute(command);

        return Response
            .status(Response.Status.CREATED)
            .entity(ProjectResponse.from(project))
            .build();
    }
}
```

### `adapter/rest/resource/TaskResource.java`

```java
package dev.quarkstack.adapter.rest.resource;

import dev.quarkstack.adapter.rest.dto.CreateTaskRequest;
import dev.quarkstack.adapter.rest.dto.MoveTaskRequest;
import dev.quarkstack.adapter.rest.dto.TaskResponse;
import dev.quarkstack.domain.port.in.CreateTaskUseCase;
import dev.quarkstack.domain.port.in.MoveTaskUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/projects/{projectId}/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tasks", description = "Gestión de tareas")
public class TaskResource {

    @Inject
    CreateTaskUseCase createTaskUseCase;

    @Inject
    MoveTaskUseCase moveTaskUseCase;

    @POST
    @Operation(summary = "Crear una nueva tarea en un proyecto")
    public Response create(
            @PathParam("projectId") String projectId,
            @Valid CreateTaskRequest request) {

        var command = new CreateTaskUseCase.Command(
            projectId,
            request.title(),
            request.description(),
            request.createdByUserId()
        );

        var task = createTaskUseCase.execute(command);

        return Response
            .status(Response.Status.CREATED)
            .entity(TaskResponse.from(task))
            .build();
    }

    @PATCH
    @Path("/{taskId}/status")
    @Operation(summary = "Mover una tarea a un nuevo estado")
    public Response move(
            @PathParam("projectId") String projectId,
            @PathParam("taskId") String taskId,
            @Valid MoveTaskRequest request) {

        var command = new MoveTaskUseCase.Command(
            taskId,
            request.newStatus(),
            request.movedByUserId()
        );

        var task = moveTaskUseCase.execute(command);

        return Response.ok(TaskResponse.from(task)).build();
    }
}
```

---

## Paso 3 — Exception Mappers

Los exception mappers convierten excepciones del dominio en respuestas HTTP con el formato correcto. Viven en el runner porque son infraestructura transversal.

### `infrastructure/exception/ErrorResponse.java`

Primero, el DTO de error que usaremos en todos los mappers:

```java
package dev.quarkstack.infrastructure.exception;

import java.time.Instant;

/**
 * Formato estándar de error para todas las respuestas de error de la API.
 */
public record ErrorResponse(
    int status,
    String error,
    String message,
    Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now());
    }
}
```

### `infrastructure/exception/IllegalArgumentExceptionMapper.java`

```java
package dev.quarkstack.infrastructure.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Convierte IllegalArgumentException (recurso no encontrado, datos inválidos)
 * en una respuesta HTTP 400 Bad Request.
 */
@Provider
public class IllegalArgumentExceptionMapper
        implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(ErrorResponse.of(
                400,
                "Bad Request",
                exception.getMessage()
            ))
            .build();
    }
}
```

### `infrastructure/exception/IllegalStateExceptionMapper.java`

```java
package dev.quarkstack.infrastructure.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Convierte IllegalStateException (transición de estado inválida, etc.)
 * en una respuesta HTTP 422 Unprocessable Entity.
 */
@Provider
public class IllegalStateExceptionMapper
        implements ExceptionMapper<IllegalStateException> {

    @Override
    public Response toResponse(IllegalStateException exception) {
        return Response
            .status(422) // Unprocessable Entity
            .type(MediaType.APPLICATION_JSON)
            .entity(ErrorResponse.of(
                422,
                "Unprocessable Entity",
                exception.getMessage()
            ))
            .build();
    }
}
```

---

## Paso 4 — Repositorios en memoria (temporales)

Estos repositorios viven en el runner y sirven hasta que conectemos PostgreSQL en el Ep 05.

### `infrastructure/repository/InMemoryProjectRepository.java`

```java
package dev.quarkstack.infrastructure.repository;

import dev.quarkstack.domain.model.Project;
import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.port.out.ProjectRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del ProjectRepository.
 * Temporal — se reemplaza por PanacheProjectRepository en el Ep 05.
 *
 * Al ser una implementación del puerto, el caso de uso no necesita
 * cambiar absolutamente nada cuando hagamos el reemplazo.
 */
public class InMemoryProjectRepository implements ProjectRepository {

    private final Map<ProjectId, Project> store = new ConcurrentHashMap<>();

    @Override
    public Project save(Project project) {
        store.put(project.id(), project);
        return project;
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Project> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(ProjectId id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(ProjectId id) {
        return store.containsKey(id);
    }
}
```

### `infrastructure/repository/InMemoryTaskRepository.java`

```java
package dev.quarkstack.infrastructure.repository;

import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskId;
import dev.quarkstack.domain.model.TaskStatus;
import dev.quarkstack.domain.port.out.TaskRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del TaskRepository.
 * Temporal — se reemplaza por PanacheTaskRepository en el Ep 05.
 */
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<TaskId, Task> store = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        store.put(task.id(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findByProjectId(ProjectId projectId) {
        return store.values().stream()
            .filter(t -> t.projectId().equals(projectId))
            .toList();
    }

    @Override
    public List<Task> findByProjectIdAndStatus(ProjectId projectId, TaskStatus status) {
        return store.values().stream()
            .filter(t -> t.projectId().equals(projectId) && t.status() == status)
            .toList();
    }

    @Override
    public void deleteById(TaskId id) {
        store.remove(id);
    }
}
```

---

## Paso 5 — Cableado CDI en el runner

Aquí conectamos todas las piezas. El runner es el único lugar que sabe qué implementación concreta usar para cada puerto.

El patrón es simple: un método `@Produces` por cada caso de uso que necesite ser inyectado.

### `infrastructure/config/UseCaseProducer.java`

```java
package dev.quarkstack.infrastructure.config;

import dev.quarkstack.application.usecase.CreateProjectService;
import dev.quarkstack.application.usecase.CreateTaskService;
import dev.quarkstack.application.usecase.MoveTaskService;
import dev.quarkstack.domain.port.in.CreateProjectUseCase;
import dev.quarkstack.domain.port.in.CreateTaskUseCase;
import dev.quarkstack.domain.port.in.MoveTaskUseCase;
import dev.quarkstack.domain.port.out.ProjectRepository;
import dev.quarkstack.domain.port.out.TaskEventPublisher;
import dev.quarkstack.domain.port.out.TaskRepository;
import dev.quarkstack.infrastructure.repository.InMemoryProjectRepository;
import dev.quarkstack.infrastructure.repository.InMemoryTaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Productor CDI que cablea los casos de uso con sus implementaciones.
 *
 * Este es el único lugar del proyecto donde se decide:
 * - Qué implementación de repositorio usar (en memoria, Panache, mock...)
 * - Cómo se construyen los casos de uso
 *
 * Cuando en el Ep 05 conectemos PostgreSQL real, solo cambiaremos
 * este archivo — nada más.
 */
@ApplicationScoped
public class UseCaseProducer {

    // -------------------------------------------------------------------------
    // Repositorios (temporales — se reemplazan en Ep 05)
    // -------------------------------------------------------------------------

    @Produces
    @ApplicationScoped
    public ProjectRepository projectRepository() {
        return new InMemoryProjectRepository();
    }

    @Produces
    @ApplicationScoped
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }

    @Produces
    @ApplicationScoped
    public TaskEventPublisher taskEventPublisher() {
        // Temporal: log en consola hasta conectar Kafka en Ep 18
        return new TaskEventPublisher() {
            @Override
            public void publishTaskCreated(dev.quarkstack.domain.model.Task task) {
                System.out.printf("[EVENT] TaskCreated: %s - %s%n",
                    task.id(), task.title());
            }

            @Override
            public void publishTaskMoved(dev.quarkstack.domain.model.Task task,
                                         dev.quarkstack.domain.model.TaskStatus previousStatus,
                                         dev.quarkstack.domain.model.UserId movedBy) {
                System.out.printf("[EVENT] TaskMoved: %s %s → %s by %s%n",
                    task.id(), previousStatus, task.status(), movedBy);
            }

            @Override
            public void publishTaskAssigned(dev.quarkstack.domain.model.Task task,
                                            dev.quarkstack.domain.model.UserId assignedBy) {
                System.out.printf("[EVENT] TaskAssigned: %s → %s by %s%n",
                    task.id(), task.assigneeId(), assignedBy);
            }
        };
    }

    // -------------------------------------------------------------------------
    // Casos de uso
    // -------------------------------------------------------------------------

    @Produces
    @ApplicationScoped
    public CreateProjectUseCase createProjectUseCase(ProjectRepository projectRepository) {
        return new CreateProjectService(projectRepository);
    }

    @Produces
    @ApplicationScoped
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository,
                                               ProjectRepository projectRepository,
                                               TaskEventPublisher eventPublisher) {
        return new CreateTaskService(taskRepository, projectRepository, eventPublisher);
    }

    @Produces
    @ApplicationScoped
    public MoveTaskUseCase moveTaskUseCase(TaskRepository taskRepository,
                                           TaskEventPublisher eventPublisher) {
        return new MoveTaskService(taskRepository, eventPublisher);
    }
}
```

---

## Paso 6 — Configurar el indexado de módulos

> ⚠️ **Atención multi-módulo:** por defecto Quarkus solo escanea (indexa) las clases del módulo donde vive el plugin — el runner. Las clases de los módulos hermanos (`ProjectResource`, `TaskResource`, los beans CDI, etc.) existen en el classpath pero Quarkus no las descubre. El resultado: los endpoints no aparecen en Swagger UI ni en el Dev UI, y los beans CDI no se registran.

La solución es declarar explícitamente qué módulos debe indexar. Agrega esto en `application.properties`:

```properties
# --- Indexado de módulos hermanos ---
# Sin esto, Quarkus no descubre los endpoints REST ni los beans CDI
# que viven en módulos distintos al runner.
quarkus.index-dependency.domain.group-id=dev.quarkstack
quarkus.index-dependency.domain.artifact-id=quarkstack-domain

quarkus.index-dependency.application.group-id=dev.quarkstack
quarkus.index-dependency.application.artifact-id=quarkstack-application

quarkus.index-dependency.adapter-rest.group-id=dev.quarkstack
quarkus.index-dependency.adapter-rest.artifact-id=quarkstack-adapter-rest

quarkus.index-dependency.adapter-persistence.group-id=dev.quarkstack
quarkus.index-dependency.adapter-persistence.artifact-id=quarkstack-adapter-persistence

quarkus.index-dependency.adapter-messaging.group-id=dev.quarkstack
quarkus.index-dependency.adapter-messaging.artifact-id=quarkstack-adapter-messaging
```

> 💡 Esta configuración va en `application.properties` (no en `application-docker.properties`) porque Quarkus necesita indexar los módulos en todos los entornos, incluido el build de producción.

También necesitas agregar la dependencia de Bean Validation en `backend/quarkstack-adapter-rest/pom.xml` — sin ella, las anotaciones `@NotBlank`, `@Size` y `@Valid` de los DTOs no funcionan:

```xml
<!-- Agregar en quarkstack-adapter-rest/pom.xml dentro de <dependencies> -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-validator</artifactId>
</dependency>
```

---

## Paso 7 — Compilar y arrancar

🪟 PowerShell, desde `backend/`:
```powershell
.\mvnw.cmd install -DskipTests
```

🐧 Bash:
```bash
./mvnw install -DskipTests
```

🪟 PowerShell, desde `backend/quarkstack-runner/`:
```powershell
..\mvnw.cmd quarkus:dev "-Dquarkus.profile=docker"
```

🐧 Bash:
```bash
../mvnw quarkus:dev -Dquarkus.profile=docker
```

---

## Paso 8 — Probar los endpoints

Abre http://localhost:8080/swagger-ui — verás los endpoints documentados automáticamente.

### Crear un proyecto

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "QuarkStack Tutorial",
    "description": "El mejor curso de Quarkus",
    "ownerId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

Respuesta esperada `201 Created`:
```json
{
  "id": "...",
  "name": "QuarkStack Tutorial",
  "description": "El mejor curso de Quarkus",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-..."
}
```

### Crear una tarea

```bash
curl -X POST http://localhost:8080/api/projects/{projectId}/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Configurar Kafka",
    "description": "Levantar el broker y crear los topics",
    "createdByUserId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

### Mover una tarea a IN_PROGRESS

```bash
curl -X PATCH http://localhost:8080/api/projects/{projectId}/tasks/{taskId}/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "IN_PROGRESS",
    "movedByUserId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

En la consola de Quarkus verás:
```
[EVENT] TaskCreated: {taskId} - Configurar Kafka
[EVENT] TaskMoved: {taskId} BACKLOG → IN_PROGRESS by {userId}
```

### Probar una transición inválida

```bash
curl -X PATCH http://localhost:8080/api/projects/{projectId}/tasks/{taskId}/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "DONE",
    "movedByUserId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

Si la tarea está en BACKLOG, verás `422 Unprocessable Entity`:
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Transición inválida: BACKLOG → DONE",
  "timestamp": "2026-..."
}
```

### Probar validaciones Bean Validation

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{"name": "", "ownerId": ""}'
```

Respuesta `400 Bad Request` automática de Quarkus con los errores de validación.

---

## ✅ Checklist del episodio

- [ ] DTOs `CreateProjectRequest`, `CreateTaskRequest`, `MoveTaskRequest` con validaciones
- [ ] DTOs `ProjectResponse`, `TaskResponse` con factory methods `from()`
- [ ] `ProjectResource` y `TaskResource` con anotaciones JAX-RS y OpenAPI
- [ ] `ErrorResponse`, `IllegalArgumentExceptionMapper`, `IllegalStateExceptionMapper`
- [ ] `InMemoryProjectRepository` e `InMemoryTaskRepository`
- [ ] `UseCaseProducer` cableando todo con CDI `@Produces`
- [ ] `quarkus-hibernate-validator` agregado en `quarkstack-adapter-rest/pom.xml`
- [ ] `quarkus.index-dependency.*` agregado en `application.properties`
- [ ] `./mvnw install -DskipTests` → BUILD SUCCESS
- [ ] `quarkus:dev` arranca sin errores
- [ ] http://localhost:8080/swagger-ui muestra los endpoints
- [ ] `POST /api/projects` → 201 Created
- [ ] `POST /api/projects/{id}/tasks` → 201 Created
- [ ] `PATCH .../status` con transición válida → 200 OK
- [ ] `PATCH .../status` con transición inválida → 422
- [ ] `POST /api/projects` con nombre vacío → 400

---

## 🧠 Lo que aprendimos

El adaptador REST no conoce el dominio directamente — solo conoce los puertos de entrada. Cuando el ep05 conecte PostgreSQL real, el `ProjectResource` no cambia ni una línea. Solo cambia el `UseCaseProducer` en el runner.

El `UseCaseProducer` es el corazón del cableado CDI. En frameworks como Spring Boot se usa `@Service` y `@Repository` directamente en las clases — aquí mantenemos las clases de dominio y aplicación libres de anotaciones, y el runner es el único responsable de ensamblar todo.

---

## ▶️ Siguiente episodio

**[Ep 04 → Persistencia con PostgreSQL y Panache](../ep04-persistencia-pg/README.md)**

Reemplazaremos los repositorios en memoria por implementaciones reales con Panache. El `UseCaseProducer` cambiará mínimamente — el resto del código no se toca.

---

*QuarkStack — Construido con ❤️ y mucho ☕*