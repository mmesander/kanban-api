package nu.youritguy.kanban_api.application.ports.in;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

public interface CreateTaskUseCase {

    Task createTask(CreateTaskCommand command);

    // Ik maak hier gebruik van een command zodat de domain niet weet hoe de inputDto eruit ziet
    // Ik maak gebruik van een record omdat deze automatisch een constructor aanmaakt
    record CreateTaskCommand(String title, String description, TaskStatus status) {}
}
