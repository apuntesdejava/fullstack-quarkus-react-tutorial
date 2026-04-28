package dev.quarkstack.infrastructure.repository;

import dev.quarkstack.domain.model.Project;
import dev.quarkstack.domain.model.ProjectId;
import dev.quarkstack.domain.port.out.ProjectRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del ProjectRepository.
 * Temporal — se reemplaza por PanacheProjectRepository en el Ep 05.
 *
 * Al ser una implementación del puerto, el caso de uso no necesita
 * cambiar absolutamente nada cuando hagamos el reemplazo.
 */
public class InMemoryProjectRepository implements ProjectRepository {

    private final Map<ProjectId, Project> store = new ConcurrentHashMap<>();

    @Override
    public Project save(Project project) {
        store.put(project.id(), project);
        return project;
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Project> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(ProjectId id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(ProjectId id) {
        return store.containsKey(id);
    }
}