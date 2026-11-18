package nu.youritguy.kanban_api.application.services;

import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String title, String description, TaskStatus status) {
        Task task = new Task();
        Instant now = Instant.now();

        task.setId(UUID.randomUUID());
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status != null ? status : TaskStatus.TO_DO);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        return taskRepository.saveTask(task);
    }
}
