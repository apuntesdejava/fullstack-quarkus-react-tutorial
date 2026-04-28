package dev.quarkstack.application.command;

public record CreateTaskCommand(
    String projectId,
    String title,
    String description,
    String createdByUserId
) {}