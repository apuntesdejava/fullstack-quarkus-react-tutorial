package dev.quarkstack.infrastructure.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Convierte IllegalArgumentException (recurso no encontrado, datos inválidos)
 * en una respuesta HTTP 400 Bad Request.
 */
@Provider
public class IllegalArgumentExceptionMapper
    implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(ErrorResponse.of(
                400,
                "Bad Request",
                exception.getMessage()
            ))
            .build();
    }
}