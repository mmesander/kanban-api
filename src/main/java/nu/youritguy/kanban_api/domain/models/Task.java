package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Task {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task() {
        this.status = TaskStatus.TO_DO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(String title, String description) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.TO_DO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(UUID id, String title, String description, TaskStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = LocalDateTime.now();
    }
}
