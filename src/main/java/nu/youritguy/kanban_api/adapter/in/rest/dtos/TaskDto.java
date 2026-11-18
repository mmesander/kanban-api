package nu.youritguy.kanban_api.adapter.in.rest.dtos;

import lombok.Getter;
import lombok.Setter;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class TaskDto {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public TaskDto(UUID id, String title, String description, TaskStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TaskDto createTaskDto(Task task) {
        return new TaskDto(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getCreatedAt(), task.getUpdatedAt());
    }
}
