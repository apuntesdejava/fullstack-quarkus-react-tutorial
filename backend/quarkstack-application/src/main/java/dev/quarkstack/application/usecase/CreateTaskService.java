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