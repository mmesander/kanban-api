package nu.youritguy.kanban_api.adapter.in.rest.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import nu.youritguy.kanban_api.adapter.in.rest.dtos.*;
import nu.youritguy.kanban_api.application.ports.in.CreateTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.DeleteTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.GetTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.PatchTaskUseCase;
import nu.youritguy.kanban_api.domain.exceptions.InvalidInputException;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import nu.youritguy.kanban_api.domain.models.TaskSummary;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static nu.youritguy.kanban_api.adapter.in.rest.dtos.TaskDto.createTaskDto;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final GetTaskUseCase getTaskUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final PatchTaskUseCase patchTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase, GetTaskUseCase getTaskUseCase,
                          PatchTaskUseCase patchTaskUseCase, DeleteTaskUseCase deleteTaskUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.getTaskUseCase = getTaskUseCase;
        this.patchTaskUseCase = patchTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<Task> tasks = getTaskUseCase.getAllTasks();

        List<TaskDto> taskDtos = tasks.stream()
                .filter(Objects::nonNull)
                .map(TaskDto::createTaskDto)
                .toList();

        return ResponseEntity.ok().body(taskDtos);
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable("id") UUID id
    ) {
        Task task = getTaskUseCase.getTaskById(new GetTaskUseCase.GetTaskByIdCommand(id));

        return ResponseEntity.ok().body(TaskDto.createTaskDto(task));
    }

    @GetMapping("/status")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(
            @Valid
            @RequestBody StatusInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        }
        List<Task> tasks = getTaskUseCase.getTasksByStatus(new GetTaskUseCase.GetTasksByStatusCommand(
                parseStatus(inputDto.getStatus())
        ));

        List<TaskDto> taskDtos = tasks.stream()
                .filter(Objects::nonNull)
                .map(TaskDto::createTaskDto)
                .toList();

        return ResponseEntity.ok().body(taskDtos);
    }

    @GetMapping("/overview")
    public ResponseEntity<TaskOverviewDto> getTaskOverview() {
        TaskOverview taskOverview = getTaskUseCase.getTaskOverview();

        return ResponseEntity.ok().body(TaskOverviewDto.createTaskOverview(taskOverview));
    }

    @GetMapping("/summary")
    public ResponseEntity<TaskSummaryDto> getTaskSummary() {
        TaskSummary taskSummary = getTaskUseCase.getTaskSummary();

        return ResponseEntity.ok().body(TaskSummaryDto.createTaskSummaryDto(taskSummary));
    }

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
                    inputDto.getTitle(), inputDto.getDescription(), parseStatus(inputDto.getStatus())));

            URI uri = buildUriId(task.getId());

            return ResponseEntity.created(uri).body(createTaskDto(task));
        }
    }

    @PatchMapping("/{id}")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskDto> patchTask(
            @PathVariable("id") UUID id,
            @Valid
            @RequestBody TaskPatchInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            Task task = patchTaskUseCase.patchTask(id, new PatchTaskUseCase.PatchTaskCommand(
                    inputDto.getTitle(), inputDto.getDescription(), parseStatus(inputDto.getStatus())));

            return ResponseEntity.ok().body(TaskDto.createTaskDto(task));
        }
    }

    @PatchMapping("/{id}/status")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskDto> moveTask(
            @PathVariable("id") UUID id,
            @Valid
            @RequestBody StatusInputDto inputDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasFieldErrors()) {
            throw new InvalidInputException(handleBindingResultError(bindingResult));
        } else {
            Task task = patchTaskUseCase.moveTask(id, new PatchTaskUseCase.MoveTaskCommand(
                    parseStatus(inputDto.getStatus())
            ));

            return ResponseEntity.ok().body(TaskDto.createTaskDto(task));
        }
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Object> deleteTask(
            @PathVariable("id") UUID id
    ) {
        deleteTaskUseCase.deleteTask(id);
        String confirmation = String.format("Deleted task [%s]", id);

        return ResponseEntity.ok().body(confirmation);
    }


    // Helpers
    private TaskStatus parseStatus(String status) {
        return StringUtils.isNotBlank(status) ? TaskStatus.retrieveEnumFromInputStatus(status.trim()) : null;
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
