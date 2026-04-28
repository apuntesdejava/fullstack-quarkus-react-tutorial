package dev.quarkstack.infrastructure.repository;

import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskId;
import dev.quarkstack.domain.model.TaskStatus;
import dev.quarkstack.domain.port.out.TaskRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del TaskRepository.
 * Temporal — se reemplaza por PanacheTaskRepository en el Ep 05.
 */
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<TaskId, Task> store = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        store.put(task.id(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findByProjectId(ProjectId projectId) {
        return store.values().stream()
            .filter(t -> t.projectId().equals(projectId))
            .toList();
    }

    @Override
    public List<Task> findByProjectIdAndStatus(ProjectId projectId, TaskStatus status) {
        return store.values().stream()
            .filter(t -> t.projectId().equals(projectId) && t.status() == status)
            .toList();
    }

    @Override
    public void deleteById(TaskId id) {
        store.remove(id);
    }
}