package nu.youritguy.kanban_api.application.ports.out;

import nu.youritguy.kanban_api.domain.models.Task;

public interface TaskRepository {
    Task saveTask(Task task);
}
