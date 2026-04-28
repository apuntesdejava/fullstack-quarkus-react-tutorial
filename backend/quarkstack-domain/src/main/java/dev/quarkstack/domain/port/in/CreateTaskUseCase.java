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