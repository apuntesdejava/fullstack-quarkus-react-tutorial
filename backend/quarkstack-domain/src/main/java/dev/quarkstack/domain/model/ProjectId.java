package dev.quarkstack.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class ProjectId {

    private final UUID value;

    private ProjectId(UUID value) {
        this.value = Objects.requireNonNull(value, "ProjectId no puede ser nulo");
    }

    public static ProjectId of(UUID value) {
        return new ProjectId(value);
    }

    public static ProjectId generate() {
        return new ProjectId(UUID.randomUUID());
    }

    public static ProjectId of(String value) {
        return new ProjectId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectId other)) return false;
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