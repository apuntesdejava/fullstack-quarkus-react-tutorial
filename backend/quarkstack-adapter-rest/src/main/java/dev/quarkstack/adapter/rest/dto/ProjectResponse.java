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