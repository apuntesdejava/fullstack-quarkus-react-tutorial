package dev.quarkstack.domain.model;

/**
 * Estados posibles de una tarea.
 * Las transiciones válidas son:
 *   BACKLOG → IN_PROGRESS → DONE
 *   DONE → IN_PROGRESS (reapertura)
 */
public enum TaskStatus {
    BACKLOG,
    IN_PROGRESS,
    DONE;

    /**
     * Verifica si la transición al nuevo estado es válida.
     * El dominio es el único que conoce las reglas de negocio.
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        return switch (this) {
            case BACKLOG, DONE -> newStatus == IN_PROGRESS;
            case IN_PROGRESS -> newStatus == DONE || newStatus == BACKLOG;
        };
    }
}