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