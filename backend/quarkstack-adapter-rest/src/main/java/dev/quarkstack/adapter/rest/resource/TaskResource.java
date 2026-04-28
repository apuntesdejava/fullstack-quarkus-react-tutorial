package dev.quarkstack.adapter.rest.resource;

import dev.quarkstack.adapter.rest.dto.CreateTaskRequest;
import dev.quarkstack.adapter.rest.dto.MoveTaskRequest;
import dev.quarkstack.adapter.rest.dto.TaskResponse;
import dev.quarkstack.domain.port.in.CreateTaskUseCase;
import dev.quarkstack.domain.port.in.MoveTaskUseCase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/projects/{projectId}/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tasks", description = "Gestión de tareas")
public class TaskResource {

    @Inject
    CreateTaskUseCase createTaskUseCase;

    @Inject
    MoveTaskUseCase moveTaskUseCase;

    @POST
    @Operation(summary = "Crear una nueva tarea en un proyecto")
    public Response create(
        @PathParam("projectId") String projectId,
        @Valid CreateTaskRequest request) {

        var command = new CreateTaskUseCase.Command(
            projectId,
            request.title(),
            request.description(),
            request.createdByUserId()
        );

        var task = createTaskUseCase.execute(command);

        return Response
            .status(Response.Status.CREATED)
            .entity(TaskResponse.from(task))
            .build();
    }

    @PATCH
    @Path("/{taskId}/status")
    @Operation(summary = "Mover una tarea a un nuevo estado")
    public Response move(
        @PathParam("projectId") String projectId,
        @PathParam("taskId") String taskId,
        @Valid MoveTaskRequest request) {

        var command = new MoveTaskUseCase.Command(
            taskId,
            request.newStatus(),
            request.movedByUserId()
        );

        var task = moveTaskUseCase.execute(command);

        return Response.ok(TaskResponse.from(task)).build();
    }
}