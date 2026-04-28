package dev.quarkstack.adapter.rest.resource;

import dev.quarkstack.adapter.rest.dto.CreateProjectRequest;
import dev.quarkstack.adapter.rest.dto.ProjectResponse;
import dev.quarkstack.domain.port.in.CreateProjectUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Adaptador de entrada REST para proyectos.
 * <br/>
 *
 * Este resource solo sabe de HTTP, DTOs y casos de uso.
 * No conoce entidades del dominio ni repositorios.
 */
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Projects", description = "Gestión de proyectos")
public class ProjectResource {

    @Inject
    CreateProjectUseCase createProjectUseCase;

    @POST
    @Operation(summary = "Crear un nuevo proyecto")
    public Response create(@Valid CreateProjectRequest request) {
        var command = new CreateProjectUseCase.Command(
            request.name(),
            request.description(),
            request.ownerId()
        );

        var project = createProjectUseCase.execute(command);

        return Response
            .status(Response.Status.CREATED)
            .entity(ProjectResponse.from(project))
            .build();
    }
}