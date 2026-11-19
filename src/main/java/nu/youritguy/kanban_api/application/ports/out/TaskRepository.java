package nu.youritguy.kanban_api.application.ports.out;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    // GET
    List<Task> getAllTasks();
//    TaskOverview getTaskOverview();
//    Optional<Task> getTaskById(UUID id);
//    List<Task> getTasksByStatus(TaskStatus status);

    // POST
    Task saveTask(Task task);

    // PUT
    // PATCH
    // DELETE
}