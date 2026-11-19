package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityMapper {
    public TaskEntity transformToEntity(Task task) {
        TaskEntity entity = new TaskEntity();

        BeanUtils.copyProperties(task, entity);

        return entity;
    }

    public Task transformToDomain(TaskEntity entity) {
        Task task = new Task();

        BeanUtils.copyProperties(entity, task);

        return task;
    }
}
