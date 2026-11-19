package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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

    public Task() {
    }

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

    public void patchTask(String title, String description, TaskStatus status) {
        if (StringUtils.isNotBlank(title)) {
            this.title = title;
        }
        if (StringUtils.isNotBlank(description)) {
            this.description = description;
        }
        if (status != null) {
            this.status = status;
        }
        this.updatedAt = Instant.now();
    }

    public void moveTask(TaskStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
