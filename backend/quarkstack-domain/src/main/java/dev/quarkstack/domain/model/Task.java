package dev.quarkstack.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Tarea dentro de un proyecto.
 * <br/>
 * Esta clase es la más importante del dominio porque:
 * - Encapsula la regla de transición de estados
 * - Genera eventos de dominio al cambiar de estado
 * - No conoce nada de bases de datos ni frameworks
 */
public class Task  {

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