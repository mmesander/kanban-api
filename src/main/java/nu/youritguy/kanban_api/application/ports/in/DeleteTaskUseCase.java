package nu.youritguy.kanban_api.application.ports.in;

import java.util.UUID;

public interface DeleteTaskUseCase {

    void deleteTask(DeleteTaskCommand command);

    record DeleteTaskCommand(UUID id) {}
}
