package nu.youritguy.kanban_api.application.ports.in;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import nu.youritguy.kanban_api.domain.models.TaskOverview;
import nu.youritguy.kanban_api.domain.models.TaskSummary;

import java.util.List;
import java.util.UUID;

public interface GetTaskUseCase {
    List<Task> getAllTasks();
    Task getTaskById(GetTaskByIdCommand command);
    List<Task> getTasksByStatus(GetTasksByStatusCommand command);
    TaskOverview getTaskOverview();
    TaskSummary getTaskSummary();

    record GetTaskByIdCommand(UUID id) {}
    record GetTasksByStatusCommand(TaskStatus status) {}
}
