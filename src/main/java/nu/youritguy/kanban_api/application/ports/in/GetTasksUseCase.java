package nu.youritguy.kanban_api.application.ports.in;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import nu.youritguy.kanban_api.domain.models.TaskOverview;

import java.util.List;
import java.util.UUID;

public interface GetTasksUseCase {
    List<Task> getTasks();
    TaskOverview getTaskSummary();
    Task getTaskById(GetTaskByIdCommand command);
    List<Task> getTasksByStatus(GetTasksByStatusCommand command);

    record GetTaskByIdCommand(UUID id) {}
    record GetTasksByStatusCommand(TaskStatus status) {}
}
