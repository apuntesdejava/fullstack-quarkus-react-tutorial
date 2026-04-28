package dev.quarkstack.infrastructure.exception;

import java.time.Instant;

/**
 * Formato estándar de error para todas las respuestas de error de la API.
 */
public record ErrorResponse(
    int status,
    String error,
    String message,
    Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now());
    }
}