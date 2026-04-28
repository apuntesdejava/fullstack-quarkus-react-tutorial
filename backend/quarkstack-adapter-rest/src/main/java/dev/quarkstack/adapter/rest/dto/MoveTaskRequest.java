package dev.quarkstack.adapter.rest.dto;

import dev.quarkstack.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record MoveTaskRequest(

    @NotNull(message = "El nuevo estado es obligatorio")
    TaskStatus newStatus,

    @NotNull(message = "El usuario que mueve la tarea es obligatorio")
    String movedByUserId
) {}