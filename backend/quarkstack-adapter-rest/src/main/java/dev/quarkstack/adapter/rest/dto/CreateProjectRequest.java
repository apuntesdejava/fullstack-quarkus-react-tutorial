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