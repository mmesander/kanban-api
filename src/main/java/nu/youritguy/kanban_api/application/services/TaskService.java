package nu.youritguy.kanban_api.application.services;

import nu.youritguy.kanban_api.application.ports.in.CreateTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.GetTaskUseCase;
import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.domain.exceptions.RecordNotFoundException;
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
    public Task getTaskById(GetTaskByIdCommand command) {
        return taskRepository.findTaskById(command.id())
                .orElseThrow(() -> new RecordNotFoundException(command.id()));
    }

    @Override
    public List<Task> getTasksByStatus(GetTasksByStatusCommand command) {
        return taskRepository.findTasksByStatus(command.status());
    }

    @Override
    public TaskOverview getTaskOverview() {
        List<Task> allTasks = taskRepository.getAllTasks();
        return TaskOverview.createTaskOverview(allTasks);
    }

    @Override
    public Task createTask(CreateTaskCommand command) {
        Task task = Task.createTask(command.title(), command.description(), command.status());
        return taskRepository.saveTask(task);
    }
}
