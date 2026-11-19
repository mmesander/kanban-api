package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskPersistenceAdapterTest {

    @Mock
    private TaskEntityRepository taskEntityRepository;

    private TaskPersistenceAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final Instant now = Instant.parse("2024-01-01T10:00:00Z");
    private TaskEntity entity;

    @BeforeEach
    void setUp() {
        // Use the real mapper so the mapping is exercised as well
        adapter = new TaskPersistenceAdapter(taskEntityRepository, new TaskEntityMapper());
        entity = new TaskEntity(id, "Title", "Desc", TaskStatus.TO_DO, now, now);
    }

    @Test
    void getAllTasksMapsEntitiesToDomain() {
        when(taskEntityRepository.findAll()).thenReturn(List.of(entity));

        List<Task> tasks = adapter.getAllTasks();

        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getId()).isEqualTo(id);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Title");
    }

    @Test
    void getAllTasksReturnsEmptyListWhenNoEntities() {
        when(taskEntityRepository.findAll()).thenReturn(List.of());

        assertThat(adapter.getAllTasks()).isEmpty();
    }

    @Test
    void findTaskByIdReturnsMappedTask() {
        when(taskEntityRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Task> result = adapter.findTaskById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getStatus()).isEqualTo(TaskStatus.TO_DO);
    }

    @Test
    void findTaskByIdReturnsEmptyWhenNotFound() {
        when(taskEntityRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(adapter.findTaskById(id)).isEmpty();
    }

    @Test
    void findTasksByStatusMapsEntities() {
        when(taskEntityRepository.findAllByStatus(TaskStatus.TO_DO)).thenReturn(List.of(entity));

        List<Task> tasks = adapter.findTasksByStatus(TaskStatus.TO_DO);

        assertThat(tasks).extracting(Task::getId).containsExactly(id);
    }

    @Test
    void countByStatusDelegatesToRepository() {
        when(taskEntityRepository.countByStatus(TaskStatus.DONE)).thenReturn(7);

        assertThat(adapter.countByStatus(TaskStatus.DONE)).isEqualTo(7);
    }

    @Test
    void saveTaskMapsToEntitySavesAndMapsBack() {
        Task task = new Task(id, "Title", "Desc", TaskStatus.IN_PROGRESS, now, now);
        when(taskEntityRepository.save(any(TaskEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Task saved = adapter.saveTask(task);

        ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
        verify(taskEntityRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(saved).usingRecursiveComparison().isEqualTo(task);
    }

    @Test
    void existsByIdDelegatesToRepository() {
        when(taskEntityRepository.existsById(id)).thenReturn(true);

        assertThat(adapter.existsById(id)).isTrue();
    }

    @Test
    void deleteTaskDelegatesToRepository() {
        adapter.deleteTask(id);

        verify(taskEntityRepository).deleteById(id);
    }
}
