package dev.quarkstack.application.command;

import dev.quarkstack.domain.model.TaskStatus;

public record MoveTaskCommand(
    String taskId,
    TaskStatus newStatus,
    String movedByUserId
) {}