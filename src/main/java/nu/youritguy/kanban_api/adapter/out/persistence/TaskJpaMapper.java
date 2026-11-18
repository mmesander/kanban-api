package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TaskJpaMapper {
    public TaskJpaEntity transformToEntity(Task task) {
        TaskJpaEntity entity = new TaskJpaEntity();

        BeanUtils.copyProperties(task, entity);

        return entity;
    }

    public Task transformToDomain(TaskJpaEntity entity) {
        Task task = new Task();

        BeanUtils.copyProperties(entity, task);

        return task;
    }
}
