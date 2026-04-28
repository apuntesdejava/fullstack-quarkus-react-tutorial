package dev.quarkstack.domain.port.out;

import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskStatus;
import dev.quarkstack.domain.model.UserId;

/**
 * Puerto de salida: contrato para publicar eventos de dominio.
 * <br/>
 * Cuando una tarea cambia de estado, el dominio lo notifica a través
 * de este puerto. El adaptador de mensajería (Kafka) lo implementa.
 * El dominio no sabe nada de Kafka ni de SmallRye.
 */
public interface TaskEventPublisher {

    void publishTaskCreated(Task task);

    void publishTaskMoved(Task task, TaskStatus previousStatus, UserId movedBy);

    void publishTaskAssigned(Task task, UserId assignedBy);
}