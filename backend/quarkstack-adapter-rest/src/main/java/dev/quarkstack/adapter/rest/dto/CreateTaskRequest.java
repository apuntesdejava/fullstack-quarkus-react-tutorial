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