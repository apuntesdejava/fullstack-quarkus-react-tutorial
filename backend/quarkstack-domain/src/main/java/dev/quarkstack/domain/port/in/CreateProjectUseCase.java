package dev.quarkstack.domain.port.in;

import dev.quarkstack.domain.model.Project;

/**
 * Puerto de entrada: caso de uso para crear un proyecto.
 * <br/>
 * Esta interfaz es el contrato que el adaptador REST invoca.
 * La implementación vive en el módulo application.
 */
public interface CreateProjectUseCase {

    Project execute(Command command);

    record Command(
        String name,
        String description,
        String ownerId
    ) {}
}