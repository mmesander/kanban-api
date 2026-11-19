package nu.youritguy.kanban_api.application.ports.out;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    // GET
    List<Task> getAllTasks();
    Optional<Task> findTaskById(UUID id);
    List<Task> findTasksByStatus(TaskStatus status);
    int countByStatus(TaskStatus status);

    // POST
    Task saveTask(Task task);

    // PUT
    // PATCH
    // DELETE
}