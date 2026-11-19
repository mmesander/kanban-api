package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.domain.models.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("h2")
public class TaskPersistenceAdapter implements TaskRepository {
    private final TaskJpaRepository taskJpaRepository;
    private final TaskJpaMapper taskJpaMapper;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository, TaskJpaMapper taskJpaMapper) {
        this.taskJpaRepository = taskJpaRepository;
        this.taskJpaMapper = taskJpaMapper;
    }

    @Override
    public List<Task> getAllTasks() {
        List<TaskJpaEntity> entities = taskJpaRepository.findAll();
        return entities.stream()
                .map(taskJpaMapper::transformToDomain)
                .toList();
    }

    @Override
    public Task saveTask(Task task) {
        TaskJpaEntity entity = taskJpaMapper.transformToEntity(task);
        TaskJpaEntity createdTask = taskJpaRepository.save(entity);
        return taskJpaMapper.transformToDomain(createdTask);
    }
}
