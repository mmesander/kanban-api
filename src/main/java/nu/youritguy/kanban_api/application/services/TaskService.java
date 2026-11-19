package nu.youritguy.kanban_api.application.services;

import nu.youritguy.kanban_api.application.ports.in.CreateTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.GetTaskUseCase;
import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;

import java.util.List;

// In de service wordt de input van de useCase ontvangen
// Het domein wordt gevraagd om een nieuwe task aan te maken
// De task wordt opgeslagen in de repo
// Het resultaat wordt geretourneerd
public class TaskService implements CreateTaskUseCase, GetTaskUseCase {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    @Override
    public Task createTask(CreateTaskCommand command) {
        Task task = Task.createTask(command.title(), command.description(), command.status());
        return taskRepository.saveTask(task);
    }
}
