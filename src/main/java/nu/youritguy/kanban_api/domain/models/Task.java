package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class Task {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Task() {}

    public Task(UUID id, String title, String description, TaskStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Task createTask(String title, String description, TaskStatus status) {
        Task task = new Task();
        Instant now = Instant.now();

        task.id = UUID.randomUUID();
        task.title = title;
        task.description = description;
        task.status = status != null ? status : TaskStatus.TO_DO;
        task.createdAt = now;
        task.updatedAt = now;

        return task;
    }
}
