package nu.youritguy.kanban_api.adapter.in.rest.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import nu.youritguy.kanban_api.adapter.in.rest.dtos.TaskInputDto;
import nu.youritguy.kanban_api.adapter.in.rest.dtos.TaskDto;
import nu.youritguy.kanban_api.application.ports.in.CreateTaskUseCase;
import nu.youritguy.kanban_api.domain.exceptions.InvalidInputException;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static nu.youritguy.kanban_api.adapter.in.rest.dtos.TaskDto.createTaskDto;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase) {
        this.createTaskUseCase = createTaskUseCase;
    }

    // CRUD Operations
    @PostMapping
    @ApiResponse(responseCode = "201", description = "Created Task")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<TaskDto> createTask(
            @Valid
            @RequestBody TaskInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            Task task = createTaskUseCase.createTask(new CreateTaskUseCase.CreateTaskCommand(
                    inputDto.getTitle(), inputDto.getDescription(), parseOptionalStatus(inputDto.getStatus())));

            URI uri = buildUriId(task.getId());

            return ResponseEntity.created(uri).body(createTaskDto(task));
        }
    }

    // Helpers
    private TaskStatus parseOptionalStatus(String status) {
        return status == null || status.isBlank() ? null : TaskStatus.retrieveEnumFromInputStatus(status.trim());
    }

    private String handleBindingResultError(BindingResult bindingResult) {
        StringBuilder newString = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            newString.append(fieldError.getField());
            newString.append(": ");
            newString.append(fieldError.getDefaultMessage());
            newString.append("\n");
        }

        return newString.toString();
    }

    private URI buildUriId(UUID taskId) {
        return URI.create((
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/" + taskId).toUriString()
        ));
    }
}
