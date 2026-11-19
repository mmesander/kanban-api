package nu.youritguy.kanban_api.application.ports.out;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;

import java.util.List;
import java.util.UUID;

public interface TaskRepository {
    // GET
    List<Task> getAllTasks();
    TaskOverview getTasksOverview();
    Task getTask(UUID id);
    Task getTaskById(UUID id);

    // POST
    Task saveTask(Task task);

    // PUT
    // PATCH
    // DELETE
}
