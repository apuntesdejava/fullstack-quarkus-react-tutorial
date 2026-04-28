package dev.quarkstack.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador único de una tarea.
 * Value Object inmutable — dos TaskId son iguales si tienen el mismo valor.
 */
public final class TaskId {

    private final UUID value;

    private TaskId(UUID value) {
        this.value = Objects.requireNonNull(value, "TaskId no puede ser nulo");
    }

    public static TaskId of(UUID value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId of(String value) {
        return new TaskId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskId other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}