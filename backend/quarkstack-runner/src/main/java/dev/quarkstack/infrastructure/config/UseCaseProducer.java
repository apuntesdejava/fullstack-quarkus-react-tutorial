package dev.quarkstack.infrastructure.config;

import dev.quarkstack.application.usecase.CreateProjectService;
import dev.quarkstack.application.usecase.CreateTaskService;
import dev.quarkstack.application.usecase.MoveTaskService;
import dev.quarkstack.domain.port.in.CreateProjectUseCase;
import dev.quarkstack.domain.port.in.CreateTaskUseCase;
import dev.quarkstack.domain.port.in.MoveTaskUseCase;
import dev.quarkstack.domain.port.out.ProjectRepository;
import dev.quarkstack.domain.port.out.TaskEventPublisher;
import dev.quarkstack.domain.port.out.TaskRepository;
import dev.quarkstack.infrastructure.repository.InMemoryProjectRepository;
import dev.quarkstack.infrastructure.repository.InMemoryTaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Productor CDI que cablea los casos de uso con sus implementaciones.
 *
 * Este es el único lugar del proyecto donde se decide:
 * - Qué implementación de repositorio usar (en memoria, Panache, mock...)
 * - Cómo se construyen los casos de uso
 *
 * Cuando en el Ep 05 conectemos PostgreSQL real, solo cambiaremos
 * este archivo — nada más.
 */
@ApplicationScoped
public class UseCaseProducer {

    // -------------------------------------------------------------------------
    // Repositorios (temporales — se reemplazan en Ep 05)
    // -------------------------------------------------------------------------

    @Produces
    @ApplicationScoped
    public ProjectRepository projectRepository() {
        return new InMemoryProjectRepository();
    }

    @Produces
    @ApplicationScoped
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }

    @Produces
    @ApplicationScoped
    public TaskEventPublisher taskEventPublisher() {
        // Temporal: log en consola hasta conectar Kafka en Ep 18
        return new TaskEventPublisher() {
            @Override
            public void publishTaskCreated(dev.quarkstack.domain.model.Task task) {
                System.out.printf("[EVENT] TaskCreated: %s - %s%n",
                    task.id(), task.title());
            }

            @Override
            public void publishTaskMoved(dev.quarkstack.domain.model.Task task,
                                         dev.quarkstack.domain.model.TaskStatus previousStatus,
                                         dev.quarkstack.domain.model.UserId movedBy) {
                System.out.printf("[EVENT] TaskMoved: %s %s → %s by %s%n",
                    task.id(), previousStatus, task.status(), movedBy);
            }

            @Override
            public void publishTaskAssigned(dev.quarkstack.domain.model.Task task,
                                            dev.quarkstack.domain.model.UserId assignedBy) {
                System.out.printf("[EVENT] TaskAssigned: %s → %s by %s%n",
                    task.id(), task.assigneeId(), assignedBy);
            }
        };
    }

    // -------------------------------------------------------------------------
    // Casos de uso
    // -------------------------------------------------------------------------

    @Produces
    @ApplicationScoped
    public CreateProjectUseCase createProjectUseCase(ProjectRepository projectRepository) {
        return new CreateProjectService(projectRepository);
    }

    @Produces
    @ApplicationScoped
    public CreateTaskUseCase createTaskUseCase(TaskRepository taskRepository,
                                               ProjectRepository projectRepository,
                                               TaskEventPublisher eventPublisher) {
        return new CreateTaskService(taskRepository, projectRepository, eventPublisher);
    }

    @Produces
    @ApplicationScoped
    public MoveTaskUseCase moveTaskUseCase(TaskRepository taskRepository,
                                           TaskEventPublisher eventPublisher) {
        return new MoveTaskService(taskRepository, eventPublisher);
    }
}