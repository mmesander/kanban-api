package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("h2")
public class TaskPersistenceAdapter implements TaskRepository {
    private final TaskEntityRepository taskEntityRepository;
    private final TaskEntityMapper taskEntityMapper;

    public TaskPersistenceAdapter(TaskEntityRepository taskEntityRepository, TaskEntityMapper taskEntityMapper) {
        this.taskEntityRepository = taskEntityRepository;
        this.taskEntityMapper = taskEntityMapper;
    }

    @Override
    public List<Task> getAllTasks() {
        List<TaskEntity> entities = taskEntityRepository.findAll();
        return entities.stream()
                .map(taskEntityMapper::transformToDomain)
                .toList();
    }

    @Override
    public Optional<Task> findTaskById(UUID id) {
        Optional<TaskEntity> entity = taskEntityRepository.findById(id);
        return entity.map(taskEntityMapper::transformToDomain);
    }

    @Override
    public List<Task> findTasksByStatus(TaskStatus status) {
        List<TaskEntity> entities = taskEntityRepository.findAllByStatus(status);
        return entities.stream()
                .map(taskEntityMapper::transformToDomain)
                .toList();
    }

    @Override
    public int countByStatus(TaskStatus status) {
        return taskEntityRepository.countByStatus(status);
    }

    @Override
    public Task saveTask(Task task) {
        TaskEntity entity = taskEntityMapper.transformToEntity(task);
        TaskEntity createdTask = taskEntityRepository.save(entity);
        return taskEntityMapper.transformToDomain(createdTask);
    }

    @Override
    public boolean existsById(UUID id) {
        return taskEntityRepository.existsById(id);
    }

    @Override
    public void deleteTask(UUID id) {
        taskEntityRepository.deleteById(id);
    }
}
