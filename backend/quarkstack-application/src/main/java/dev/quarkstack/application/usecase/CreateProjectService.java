package dev.quarkstack.application.usecase;

import dev.quarkstack.domain.model.Project;
import dev.quarkstack.domain.model.UserId;
import dev.quarkstack.domain.port.in.CreateProjectUseCase;
import dev.quarkstack.domain.port.out.ProjectRepository;

/**
 * Implementación del caso de uso "Crear proyecto".
 * <br/>
 * Observa que esta clase:
 * - Implementa un puerto de entrada (CreateProjectUseCase)
 * - Depende de un puerto de salida (ProjectRepository)
 * - No tiene ninguna anotación de Quarkus ni Jakarta
 * - Es 100% testeable sin levantar el framework
 */
public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepository projectRepository;

    // Inyección por constructor — sin @Inject, sin CDI aquí
    public CreateProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project execute(Command command) {
        var project = Project.create(
            command.name(),
            command.description(),
            UserId.of(command.ownerId())
        );

        return projectRepository.save(project);
    }
}