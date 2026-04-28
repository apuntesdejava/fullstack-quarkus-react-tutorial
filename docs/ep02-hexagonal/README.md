# Episodio 02 — Arquitectura Hexagonal: Dominio, Puertos y Casos de Uso

> **Rama:** `ep/02-hexagonal` | **Tag:** `ep-02`

---

## 🎯 Objetivo

Al terminar este episodio tendrás:

- Las entidades y Value Objects del dominio de QuarkStack
- Los puertos de entrada (casos de uso) y salida (repositorios, eventos)
- La implementación de los primeros casos de uso en el módulo `application`
- La verificación de que el compilador hace cumplir las reglas de la arquitectura


---

## 📁 Archivos de este episodio

```
backend/
│
├── quarkstack-domain/src/main/java/dev/quarkstack/domain/
│   ├── model/
│   │   ├── Project.java
│   │   ├── Task.java
│   │   ├── User.java
│   │   ├── TaskStatus.java
│   │   ├── ProjectId.java
│   │   ├── TaskId.java
│   │   └── UserId.java
│   └── port/
│       ├── in/
│       │   ├── CreateProjectUseCase.java
│       │   ├── CreateTaskUseCase.java
│       │   └── MoveTaskUseCase.java
│       └── out/
│           ├── ProjectRepository.java
│           ├── TaskRepository.java
│           └── TaskEventPublisher.java
│
└── quarkstack-application/src/main/java/dev/quarkstack/application/
    ├── usecase/
    │   ├── CreateProjectService.java
    │   ├── CreateTaskService.java
    │   └── MoveTaskService.java
    └── command/
        ├── CreateProjectCommand.java
        ├── CreateTaskCommand.java
        └── MoveTaskCommand.java
```

---

## La arquitectura en una imagen

Antes de escribir código, repasemos el flujo completo que vamos a construir a lo largo del curso para tener siempre el mapa en mente:

```
┌───────────────────────────────────────────────────────────────────────┐
│                    EXTERIOR (Frameworks)                              │
│                                                                       │
│   HTTP Request ──► [REST Adapter]      [Persistence Adapter] ──► DB   │
│   Kafka Event ──► [Messaging Adapter]  [Messaging Adapter]  ──► Kafka │
│                          │                       ▲                    │
└──────────────────────────┼───────────────────────┼────────────────────┘
                           │  Puerto IN            │  Puerto OUT
                           ▼                       │
┌─────────────────────────────────────────────────────────────────┐
│                    INTERIOR (Tu código)                         │
│                                                                 │
│   [Application]  ─────────────────────────────► [Domain]        │
│   Casos de uso        implementa puertos        Entidades       │
│   CreateTaskService   llama a repositorios      Task            │
│   MoveTaskService     publica eventos           Project         │
│                                                 Value Objects   │
└─────────────────────────────────────────────────────────────────┘
```

**La regla que nunca se rompe:** las dependencias solo apuntan hacia adentro. El dominio no sabe nada de nadie. Los adaptadores saben del interior, pero el interior no sabe de los adaptadores.

---

## El dominio de QuarkStack

Antes de escribir código necesitamos modelar el negocio. QuarkStack es un gestor de tareas colaborativo, así que nuestro dominio tiene estas responsabilidades:

- Un **Project** agrupa tareas y tiene miembros
- Una **Task** tiene un título, descripción, estado y puede ser asignada a un usuario
- Un **User** puede crear proyectos, crear tareas y ser asignado a ellas
- Una tarea puede moverse entre estados: `BACKLOG → IN_PROGRESS → DONE`

---

## Paso 1 — Value Objects

Los Value Objects son objetos inmutables que representan conceptos del dominio sin identidad propia. Empezamos por los identificadores tipados.

¿Por qué IDs tipados y no simplemente `UUID`? Porque esto:

```java
// ❌ Sin tipos: el compilador no puede ayudarte
void assignTask(UUID taskId, UUID userId) { ... }
assignTask(userId, taskId); // Error lógico, pero compila feliz

// ✅ Con tipos: el compilador te protege
void assignTask(TaskId taskId, UserId userId) { ... }
assignTask(userId, taskId); // ERROR DE COMPILACIÓN ✓
```

### `domain/model/TaskId.java`

```java
package dev.quarkstack.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador único de una tarea.
 * Value Object inmutable — dos TaskId son iguales si tienen el mismo valor.
 */
public final class TaskId {

    private final UUID value;

    private TaskId(UUID value) {
        this.value = Objects.requireNonNull(value, "TaskId no puede ser nulo");
    }

    public static TaskId of(UUID value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId of(String value) {
        return new TaskId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskId other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
```

### `domain/model/ProjectId.java`

```java
package dev.quarkstack.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class ProjectId {

    private final UUID value;

    private ProjectId(UUID value) {
        this.value = Objects.requireNonNull(value, "ProjectId no puede ser nulo");
    }

    public static ProjectId of(UUID value) {
        return new ProjectId(value);
    }

    public static ProjectId generate() {
        return new ProjectId(UUID.randomUUID());
    }

    public static ProjectId of(String value) {
        return new ProjectId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectId other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
```

### `domain/model/UserId.java`

```java
package dev.quarkstack.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class UserId {

    private final UUID value;

    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId no puede ser nulo");
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
```

> 💡 **¿Por qué no usamos `record` de Java?** Los `record` son perfectos para Value Objects y los usaremos en casos donde la brevedad ayuda. Para los IDs preferimos la clase completa porque el constructor privado con factory methods (`of`, `generate`) da más control. En las entidades sí usaremos `record` para los objetos simples.

---

## Paso 2 — Enum TaskStatus

### `domain/model/TaskStatus.java`

```java
package dev.quarkstack.domain.model;

/**
 * Estados posibles de una tarea.
 * Las transiciones válidas son:
 *   BACKLOG → IN_PROGRESS → DONE
 *   DONE → IN_PROGRESS (reapertura)
 */
public enum TaskStatus {
    BACKLOG,
    IN_PROGRESS,
    DONE;

    /**
     * Verifica si la transición al nuevo estado es válida.
     * El dominio es el único que conoce las reglas de negocio.
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        return switch (this) {
            case BACKLOG     -> newStatus == IN_PROGRESS;
            case IN_PROGRESS -> newStatus == DONE || newStatus == BACKLOG;
            case DONE        -> newStatus == IN_PROGRESS;
        };
    }
}
```

---

## Paso 3 — Entidades del dominio

Las entidades tienen identidad propia y encapsulan las reglas de negocio. Nota que no tienen ninguna anotación de framework — son Java puro.

### `domain/model/User.java`

```java
package dev.quarkstack.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Usuario del sistema.
 * Entidad del dominio — Java puro, sin anotaciones de framework.
 */
public class User {

    private final UserId id;
    private String name;
    private String email;
    private final Instant createdAt;

    public User(UserId id, String name, String email, Instant createdAt) {
        this.id        = Objects.requireNonNull(id, "id requerido");
        this.name      = validateName(name);
        this.email     = validateEmail(email);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt requerido");
    }

    /** Factory method para crear un nuevo usuario */
    public static User create(String name, String email) {
        return new User(UserId.generate(), name, email, Instant.now());
    }

    public void updateName(String name) {
        this.name = validateName(name);
    }

    // --- Reglas de negocio del dominio ---

    private static String validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío");
        if (name.length() > 100)
            throw new IllegalArgumentException("El nombre no puede superar los 100 caracteres");
        return name.trim();
    }

    private static String validateEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("El email no puede estar vacío");
        if (!email.contains("@"))
            throw new IllegalArgumentException("Email inválido: " + email);
        return email.trim().toLowerCase();
    }

    // --- Getters ---
    public UserId id()        { return id; }
    public String name()      { return name; }
    public String email()     { return email; }
    public Instant createdAt(){ return createdAt; }
}
```

### `domain/model/Project.java`

```java
package dev.quarkstack.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Proyecto que agrupa tareas.
 * Entidad raíz del agregado Project.
 */
public class Project {

    private final ProjectId id;
    private String name;
    private String description;
    private final UserId ownerId;
    private final Instant createdAt;

    public Project(ProjectId id,
                   String name,
                   String description,
                   UserId ownerId,
                   Instant createdAt) {
        this.id          = Objects.requireNonNull(id, "id requerido");
        this.name        = validateName(name);
        this.description = description != null ? description.trim() : "";
        this.ownerId     = Objects.requireNonNull(ownerId, "ownerId requerido");
        this.createdAt   = Objects.requireNonNull(createdAt, "createdAt requerido");
    }

    /** Factory method para crear un nuevo proyecto */
    public static Project create(String name, String description, UserId ownerId) {
        return new Project(
            ProjectId.generate(),
            name,
            description,
            ownerId,
            Instant.now()
        );
    }

    public void rename(String newName) {
        this.name = validateName(newName);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío");
        if (name.length() > 200)
            throw new IllegalArgumentException("El nombre no puede superar los 200 caracteres");
        return name.trim();
    }

    // --- Getters ---
    public ProjectId id()         { return id; }
    public String name()          { return name; }
    public String description()   { return description; }
    public UserId ownerId()       { return ownerId; }
    public Instant createdAt()    { return createdAt; }
}
```

### `domain/model/Task.java`

La entidad más importante del dominio. Aquí vive la regla de transición de estados.

```java
package dev.quarkstack.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Tarea dentro de un proyecto.
 *
 * Esta clase es la más importante del dominio porque:
 * - Encapsula la regla de transición de estados
 * - Genera eventos de dominio al cambiar de estado
 * - No conoce nada de bases de datos ni frameworks
 */
public class Task {

    private final TaskId id;
    private final ProjectId projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private UserId assigneeId;  // puede ser null (tarea sin asignar)
    private final UserId createdBy;
    private final Instant createdAt;
    private Instant updatedAt;

    public Task(TaskId id,
                ProjectId projectId,
                String title,
                String description,
                TaskStatus status,
                UserId assigneeId,
                UserId createdBy,
                Instant createdAt,
                Instant updatedAt) {
        this.id          = Objects.requireNonNull(id, "id requerido");
        this.projectId   = Objects.requireNonNull(projectId, "projectId requerido");
        this.title       = validateTitle(title);
        this.description = description != null ? description.trim() : "";
        this.status      = Objects.requireNonNull(status, "status requerido");
        this.assigneeId  = assigneeId; // nullable
        this.createdBy   = Objects.requireNonNull(createdBy, "createdBy requerido");
        this.createdAt   = Objects.requireNonNull(createdAt, "createdAt requerido");
        this.updatedAt   = Objects.requireNonNull(updatedAt, "updatedAt requerido");
    }

    /** Factory method para crear una nueva tarea */
    public static Task create(ProjectId projectId,
                              String title,
                              String description,
                              UserId createdBy) {
        Instant now = Instant.now();
        return new Task(
            TaskId.generate(),
            projectId,
            title,
            description,
            TaskStatus.BACKLOG,   // toda tarea nueva empieza en BACKLOG
            null,                 // sin asignar por defecto
            createdBy,
            now,
            now
        );
    }

    /**
     * Mueve la tarea a un nuevo estado.
     * Esta es la regla de negocio más importante del dominio.
     *
     * @throws IllegalStateException si la transición no es válida
     */
    public void moveTo(TaskStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "Transición inválida: %s → %s".formatted(this.status, newStatus)
            );
        }
        this.status    = newStatus;
        this.updatedAt = Instant.now();
    }

    /**
     * Asigna la tarea a un usuario.
     */
    public void assignTo(UserId userId) {
        this.assigneeId = Objects.requireNonNull(userId, "userId requerido");
        this.updatedAt  = Instant.now();
    }

    /**
     * Desasigna la tarea.
     */
    public void unassign() {
        this.assigneeId = null;
        this.updatedAt  = Instant.now();
    }

    public void updateTitle(String title) {
        this.title     = validateTitle(title);
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description != null ? description.trim() : "";
        this.updatedAt   = Instant.now();
    }

    private static String validateTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío");
        if (title.length() > 300)
            throw new IllegalArgumentException("El título no puede superar los 300 caracteres");
        return title.trim();
    }

    // --- Getters ---
    public TaskId id()           { return id; }
    public ProjectId projectId() { return projectId; }
    public String title()        { return title; }
    public String description()  { return description; }
    public TaskStatus status()   { return status; }
    public UserId assigneeId()   { return assigneeId; }
    public UserId createdBy()    { return createdBy; }
    public Instant createdAt()   { return createdAt; }
    public Instant updatedAt()   { return updatedAt; }
    public boolean isAssigned()  { return assigneeId != null; }
}
```

---

## Paso 4 — Puertos de salida (out)

Los puertos de salida son las interfaces que el dominio/aplicación necesita del exterior. Las define el interior, las implementa el exterior.

### `domain/port/out/ProjectRepository.java`

```java
package dev.quarkstack.domain.port.out;

import dev.quarkstack.domain.model.Project;
import dev.quarkstack.domain.model.ProjectId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato para persistir y recuperar proyectos.
 *
 * Esta interfaz vive en el dominio pero la implementan los adaptadores
 * de persistencia (PostgreSQL, en memoria para tests, etc.).
 * El dominio nunca sabe qué tecnología hay detrás.
 */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(ProjectId id);

    List<Project> findAll();

    void deleteById(ProjectId id);

    boolean existsById(ProjectId id);
}
```

### `domain/port/out/TaskRepository.java`

```java
package dev.quarkstack.domain.port.out;

import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskId;
import dev.quarkstack.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato para persistir y recuperar tareas.
 */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(TaskId id);

    List<Task> findByProjectId(ProjectId projectId);

    List<Task> findByProjectIdAndStatus(ProjectId projectId, TaskStatus status);

    void deleteById(TaskId id);
}
```

### `domain/port/out/TaskEventPublisher.java`

```java
package dev.quarkstack.domain.port.out;

import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskStatus;
import dev.quarkstack.domain.model.UserId;

/**
 * Puerto de salida: contrato para publicar eventos de dominio.
 *
 * Cuando una tarea cambia de estado, el dominio lo notifica a través
 * de este puerto. El adaptador de mensajería (Kafka) lo implementa.
 * El dominio no sabe nada de Kafka ni de SmallRye.
 */
public interface TaskEventPublisher {

    void publishTaskCreated(Task task);

    void publishTaskMoved(Task task, TaskStatus previousStatus, UserId movedBy);

    void publishTaskAssigned(Task task, UserId assignedBy);
}
```

---

## Paso 5 — Puertos de entrada (in)

Los puertos de entrada definen los casos de uso disponibles. Son las únicas "puertas" por las que el exterior puede interactuar con el dominio.

### `domain/port/in/CreateProjectUseCase.java`

```java
package dev.quarkstack.domain.port.in;

import dev.quarkstack.domain.model.Project;

/**
 * Puerto de entrada: caso de uso para crear un proyecto.
 *
 * Esta interfaz es el contrato que el adaptador REST invoca.
 * La implementación vive en el módulo application.
 */
public interface CreateProjectUseCase {

    Project execute(Command command);

    record Command(
        String name,
        String description,
        String ownerId
    ) {}
}
```

### `domain/port/in/CreateTaskUseCase.java`

```java
package dev.quarkstack.domain.port.in;

import dev.quarkstack.domain.model.Task;

/**
 * Puerto de entrada: caso de uso para crear una tarea dentro de un proyecto.
 */
public interface CreateTaskUseCase {

    Task execute(Command command);

    record Command(
        String projectId,
        String title,
        String description,
        String createdByUserId
    ) {}
}
```

### `domain/port/in/MoveTaskUseCase.java`

```java
package dev.quarkstack.domain.port.in;

import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskStatus;

/**
 * Puerto de entrada: caso de uso para mover una tarea a un nuevo estado.
 * Este es el caso de uso central del tablero Kanban.
 */
public interface MoveTaskUseCase {

    Task execute(Command command);

    record Command(
        String taskId,
        TaskStatus newStatus,
        String movedByUserId
    ) {}
}
```

---

## Paso 6 — Casos de uso (módulo application)

Los casos de uso orquestan el dominio. Invocan repositorios, aplican reglas de negocio y publican eventos. Viven en `quarkstack-application`, no en `quarkstack-domain`.

### `application/command/CreateProjectCommand.java`

> Los `Command` del módulo application son objetos de transferencia internos, distintos a los `record Command` anidados en los puertos. Los puertos usan records simples; si en el futuro los comandos necesitan validación o comportamiento propio, ya están separados.

```java
package dev.quarkstack.application.command;

/**
 * Comando para crear un proyecto.
 * Objeto de transferencia entre el adaptador REST y el caso de uso.
 */
public record CreateProjectCommand(
    String name,
    String description,
    String ownerId
) {}
```

### `application/command/CreateTaskCommand.java`

```java
package dev.quarkstack.application.command;

public record CreateTaskCommand(
    String projectId,
    String title,
    String description,
    String createdByUserId
) {}
```

### `application/command/MoveTaskCommand.java`

```java
package dev.quarkstack.application.command;

import dev.quarkstack.domain.model.TaskStatus;

public record MoveTaskCommand(
    String taskId,
    TaskStatus newStatus,
    String movedByUserId
) {}
```

### `application/usecase/CreateProjectService.java`

```java
package dev.quarkstack.application.usecase;

import dev.quarkstack.domain.model.Project;
import dev.quarkstack.domain.model.UserId;
import dev.quarkstack.domain.port.in.CreateProjectUseCase;
import dev.quarkstack.domain.port.out.ProjectRepository;

/**
 * Implementación del caso de uso "Crear proyecto".
 *
 * Observa que esta clase:
 * - Implementa un puerto de entrada (CreateProjectUseCase)
 * - Depende de un puerto de salida (ProjectRepository)
 * - No tiene ninguna anotación de Quarkus ni Jakarta
 * - Es 100% testeable sin levantar el framework
 */
public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepository projectRepository;

    // Inyección por constructor — sin @Inject, sin CDI aquí
    public CreateProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project execute(Command command) {
        var project = Project.create(
            command.name(),
            command.description(),
            UserId.of(command.ownerId())
        );

        return projectRepository.save(project);
    }
}
```

### `application/usecase/CreateTaskService.java`

```java
package dev.quarkstack.application.usecase;

import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.UserId;
import dev.quarkstack.domain.port.in.CreateTaskUseCase;
import dev.quarkstack.domain.port.out.ProjectRepository;
import dev.quarkstack.domain.port.out.TaskEventPublisher;
import dev.quarkstack.domain.port.out.TaskRepository;

/**
 * Implementación del caso de uso "Crear tarea".
 */
public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskEventPublisher eventPublisher;

    public CreateTaskService(TaskRepository taskRepository,
                             ProjectRepository projectRepository,
                             TaskEventPublisher eventPublisher) {
        this.taskRepository    = taskRepository;
        this.projectRepository = projectRepository;
        this.eventPublisher    = eventPublisher;
    }

    @Override
    public Task execute(Command command) {
        var projectId = ProjectId.of(command.projectId());

        // Verificar que el proyecto existe — regla de negocio
        projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Proyecto no encontrado: " + command.projectId()
            ));

        var task = Task.create(
            projectId,
            command.title(),
            command.description(),
            UserId.of(command.createdByUserId())
        );

        var savedTask = taskRepository.save(task);

        // Publicar evento de dominio — el adaptador de Kafka lo recibe
        eventPublisher.publishTaskCreated(savedTask);

        return savedTask;
    }
}
```

### `application/usecase/MoveTaskService.java`

```java
package dev.quarkstack.application.usecase;

import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskId;
import dev.quarkstack.domain.model.TaskStatus;
import dev.quarkstack.domain.model.UserId;
import dev.quarkstack.domain.port.in.MoveTaskUseCase;
import dev.quarkstack.domain.port.out.TaskEventPublisher;
import dev.quarkstack.domain.port.out.TaskRepository;

/**
 * Implementación del caso de uso "Mover tarea".
 * Este es el caso de uso más importante del tablero Kanban.
 */
public class MoveTaskService implements MoveTaskUseCase {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher eventPublisher;

    public MoveTaskService(TaskRepository taskRepository,
                           TaskEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Task execute(Command command) {
        var taskId   = TaskId.of(command.taskId());
        var movedBy  = UserId.of(command.movedByUserId());
        TaskStatus previousStatus;

        // 1. Encontrar la tarea
        var task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Tarea no encontrada: " + command.taskId()
            ));

        // 2. Guardar el estado anterior para el evento
        previousStatus = task.status();

        // 3. Mover la tarea — la entidad valida la transición
        //    Si la transición es inválida, Task.moveTo() lanza IllegalStateException
        task.moveTo(command.newStatus());

        // 4. Persistir el nuevo estado
        var savedTask = taskRepository.save(task);

        // 5. Publicar el evento de dominio
        eventPublisher.publishTaskMoved(savedTask, previousStatus, movedBy);

        return savedTask;
    }
}
```

---

## Paso 7 — Verificar que la arquitectura se cumple

Este es el momento más satisfactorio del episodio. Intentemos romper la arquitectura a propósito para ver cómo el compilador nos detiene.

Intenta agregar temporalmente esta importación en `Task.java`:

```java
// domain/model/Task.java
import io.quarkus.hibernate.orm.panache.PanacheEntity; // ← añade esta línea

public class Task extends PanacheEntity { ... }  // ← y esta
```

Compila desde `backend/`:

🪟 PowerShell:
```powershell
.\mvnw.cmd compile
```

🐧 Bash:
```bash
./mvnw compile
```

Verás:

```
[ERROR] .../domain/model/Task.java:[3,8] error:
  package io.quarkus.hibernate.orm.panache does not exist
[ERROR] BUILD FAILURE
```

**La arquitectura está protegida por el compilador.** No es cuestión de disciplina. Revierte el cambio y compila de nuevo — ahora pasa sin problemas.

---

## Paso 8 — Compilar el proyecto

🪟 PowerShell, desde `backend/`:
```powershell
.\mvnw.cmd install -DskipTests
```

🐧 Bash:
```bash
./mvnw install -DskipTests
```

Resultado esperado:
```
[INFO] QuarkStack :: Domain ................. SUCCESS
[INFO] QuarkStack :: Application ............ SUCCESS
[INFO] QuarkStack :: Adapter :: REST ........ SUCCESS
[INFO] QuarkStack :: Adapter :: Persistence . SUCCESS
[INFO] QuarkStack :: Adapter :: Messaging ... SUCCESS
[INFO] QuarkStack :: Runner ................. SUCCESS
[INFO] BUILD SUCCESS
```

---

## ✅ Checklist del episodio

- [ ] Value Objects `TaskId`, `ProjectId`, `UserId` creados en `quarkstack-domain`
- [ ] Entidades `User`, `Project`, `Task` con reglas de negocio encapsuladas
- [ ] `TaskStatus` con validación de transiciones
- [ ] Puertos de salida: `ProjectRepository`, `TaskRepository`, `TaskEventPublisher`
- [ ] Puertos de entrada: `CreateProjectUseCase`, `CreateTaskUseCase`, `MoveTaskUseCase`
- [ ] Casos de uso: `CreateProjectService`, `CreateTaskService`, `MoveTaskService`
- [ ] Verificación: intentar importar Quarkus en `domain` → error de compilación ✓
- [ ] `./mvnw install -DskipTests` → BUILD SUCCESS

---

## 🧠 Lo que aprendimos

La arquitectura hexagonal no es solo una forma de organizar carpetas. Es un contrato arquitectónico donde:

- El **dominio** es el corazón: entidades con comportamiento, no anémicas
- Los **puertos** son los contratos: el interior los define, el exterior los cumple
- Los **casos de uso** orquestan sin conocer detalles técnicos
- El **compilador** hace cumplir las reglas — no la documentación, no el code review

En el próximo episodio conectaremos este dominio con el mundo exterior: crearemos el adaptador REST con JAX-RS que invoca los casos de uso a través de los puertos de entrada.

---

## ▶️ Siguiente episodio

**[Ep 03 → Adaptador REST: JAX-RS, OpenAPI y validaciones](../ep03-rest-adapter/README.md)**

---

*QuarkStack — Construido con ❤️ y mucho ☕*