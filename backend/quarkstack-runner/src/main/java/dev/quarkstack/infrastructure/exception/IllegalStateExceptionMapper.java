package dev.quarkstack.infrastructure.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Convierte IllegalStateException (transición de estado inválida, etc.)
 * en una respuesta HTTP 422 Unprocessable Entity.
 */
@Provider
public class IllegalStateExceptionMapper
    implements ExceptionMapper<IllegalStateException> {

    @Override
    public Response toResponse(IllegalStateException exception) {
        return Response
            .status(422) // Unprocessable Entity
            .type(MediaType.APPLICATION_JSON)
            .entity(ErrorResponse.of(
                422,
                "Unprocessable Entity",
                exception.getMessage()
            ))
            .build();
    }
}