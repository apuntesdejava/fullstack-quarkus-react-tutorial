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