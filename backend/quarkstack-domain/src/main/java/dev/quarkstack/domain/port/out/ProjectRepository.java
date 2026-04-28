package dev.quarkstack.domain.port.out;

import dev.quarkstack.domain.model.Project;
import dev.quarkstack.domain.model.ProjectId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: contrato para persistir y recuperar proyectos.
 * <br/>
 * Esta interfaz vive en el dominio pero la implementan los adaptadores
 * de persistencia (PostgreSQL, en memoria para tests, etc.).
 * El dominio nunca sabe qué tecnología hay detrás.
 */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(ProjectId id);

    List<Project> findAll();

    void deleteById(ProjectId id);

    boolean existsById(ProjectId id);
}