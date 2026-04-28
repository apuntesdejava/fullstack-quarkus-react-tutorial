package dev.quarkstack.domain.port.out;

import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskId;
import dev.quarkstack.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato para persistir y recuperar tareas.
 */
public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(TaskId id);

    List<Task> findByProjectId(ProjectId projectId);

    List<Task> findByProjectIdAndStatus(ProjectId projectId, TaskStatus status);

    void deleteById(TaskId id);
}