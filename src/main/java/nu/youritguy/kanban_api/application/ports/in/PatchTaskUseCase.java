package nu.youritguy.kanban_api.application.ports.in;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

import java.util.UUID;

public interface PatchTaskUseCase {

    Task patchTask(UUID id, PatchTaskCommand command);
    Task moveTask(UUID id, MoveTaskCommand command);

    record PatchTaskCommand(String title, String description, TaskStatus status) {}
    record MoveTaskCommand(TaskStatus status) {}
}
