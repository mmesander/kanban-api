package nu.youritguy.kanban_api.application.ports.in;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

public interface CreateTaskUseCase {

    Task createTask(CreateTaskCommand command);

    // Ik maak hier gebruik van een command zodat de kern de json niet kent
    record CreateTaskCommand(String title, String description, TaskStatus status) {}
}
