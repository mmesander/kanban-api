package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskEntityMapperTest {

    private final TaskEntityMapper mapper = new TaskEntityMapper();

    private final UUID id = UUID.randomUUID();
    private final Instant created = Instant.parse("2024-01-01T10:00:00Z");
    private final Instant updated = Instant.parse("2024-01-02T10:00:00Z");

    @Test
    void transformToEntityCopiesAllFields() {
        Task task = new Task(id, "Title", "Desc", TaskStatus.IN_PROGRESS, created, updated);

        TaskEntity entity = mapper.transformToEntity(task);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo("Title");
        assertThat(entity.getDescription()).isEqualTo("Desc");
        assertThat(entity.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(entity.getCreatedAt()).isEqualTo(created);
        assertThat(entity.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void transformToDomainCopiesAllFields() {
        TaskEntity entity = new TaskEntity(id, "Title", null, TaskStatus.DONE, created, updated);

        Task task = mapper.transformToDomain(entity);

        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getTitle()).isEqualTo("Title");
        assertThat(task.getDescription()).isNull();
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getCreatedAt()).isEqualTo(created);
        assertThat(task.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void roundTripPreservesData() {
        Task original = Task.createTask("Round trip", "Desc", TaskStatus.TO_DO);

        Task result = mapper.transformToDomain(mapper.transformToEntity(original));

        assertThat(result).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void taskEntityNoArgsConstructorAndSetters() {
        TaskEntity entity = new TaskEntity();
        assertThat(entity.getId()).isNull();

        entity.setId(id);
        entity.setTitle("T");
        entity.setDescription("D");
        entity.setStatus(TaskStatus.TO_DO);
        entity.setCreatedAt(created);
        entity.setUpdatedAt(updated);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo("T");
        assertThat(entity.getDescription()).isEqualTo("D");
        assertThat(entity.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(entity.getCreatedAt()).isEqualTo(created);
        assertThat(entity.getUpdatedAt()).isEqualTo(updated);
    }
}
