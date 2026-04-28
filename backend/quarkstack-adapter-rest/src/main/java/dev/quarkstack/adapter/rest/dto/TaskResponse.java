package dev.quarkstack.adapter.rest.dto;

import dev.quarkstack.domain.model.Task;
import dev.quarkstack.domain.model.TaskStatus;

import java.time.Instant;

public record TaskResponse(
    String id,
    String projectId,
    String title,
    String description,
    TaskStatus status,
    String assigneeId,
    String createdBy,
    Instant createdAt,
    Instant updatedAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.id().toString(),
            task.projectId().toString(),
            task.title(),
            task.description(),
            task.status(),
            task.assigneeId() != null ? task.assigneeId().toString() : null,
            task.createdBy().toString(),
            task.createdAt(),
            task.updatedAt()
        );
    }
}