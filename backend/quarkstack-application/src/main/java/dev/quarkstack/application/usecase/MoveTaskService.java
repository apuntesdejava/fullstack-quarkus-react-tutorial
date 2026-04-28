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