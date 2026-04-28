package dev.quarkstack.application.command;

/**
 * Comando para crear un proyecto.
 * Objeto de transferencia entre el adaptador REST y el caso de uso.
 */
public record CreateProjectCommand(
    String name,
    String description,
    String ownerId
) {}