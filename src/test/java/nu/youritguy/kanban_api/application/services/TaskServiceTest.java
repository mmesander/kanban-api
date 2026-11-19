package nu.youritguy.kanban_api.application.services;

import nu.youritguy.kanban_api.application.ports.in.CreateTaskUseCase.CreateTaskCommand;
import nu.youritguy.kanban_api.application.ports.in.GetTaskUseCase.GetTaskByIdCommand;
import nu.youritguy.kanban_api.application.ports.in.GetTaskUseCase.GetTasksByStatusCommand;
import nu.youritguy.kanban_api.application.ports.in.PatchTaskUseCase.MoveTaskCommand;
import nu.youritguy.kanban_api.application.ports.in.PatchTaskUseCase.PatchTaskCommand;
import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.domain.exceptions.RecordNotFoundException;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import nu.youritguy.kanban_api.domain.models.TaskSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    private final UUID id = UUID.randomUUID();
    private Task existingTask;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
        Instant past = Instant.parse("2024-01-01T10:00:00Z");
        existingTask = new Task(id, "Title", "Desc", TaskStatus.TO_DO, past, past);
    }

    @Nested
    class GetTasks {
        @Test
        void getAllTasksReturnsRepositoryResult() {
            when(taskRepository.getAllTasks()).thenReturn(List.of(existingTask));

            assertThat(taskService.getAllTasks()).containsExactly(existingTask);
        }

        @Test
        void getTaskByIdReturnsTaskWhenFound() {
            when(taskRepository.findTaskById(id)).thenReturn(Optional.of(existingTask));

            assertThat(taskService.getTaskById(new GetTaskByIdCommand(id))).isSameAs(existingTask);
        }

        @Test
        void getTaskByIdThrowsWhenNotFound() {
            when(taskRepository.findTaskById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.getTaskById(new GetTaskByIdCommand(id)))
                    .isInstanceOf(RecordNotFoundException.class)
                    .hasMessage("Task [" + id + "] not found");
        }

        @Test
        void getTasksByStatusDelegatesToRepository() {
            when(taskRepository.findTasksByStatus(TaskStatus.DONE)).thenReturn(List.of(existingTask));

            assertThat(taskService.getTasksByStatus(new GetTasksByStatusCommand(TaskStatus.DONE)))
                    .containsExactly(existingTask);
        }

        @Test
        void getTaskOverviewGroupsAllTasks() {
            Task inProgress = Task.createTask("busy", null, TaskStatus.IN_PROGRESS);
            Task done = Task.createTask("done", null, TaskStatus.DONE);
            when(taskRepository.getAllTasks()).thenReturn(List.of(existingTask, inProgress, done));

            TaskOverview overview = taskService.getTaskOverview();

            assertThat(overview.getToDo()).containsExactly(existingTask);
            assertThat(overview.getInProgress()).containsExactly(inProgress);
            assertThat(overview.getDone()).containsExactly(done);
        }

        @Test
        void getTaskSummaryCountsPerStatusAndTotal() {
            when(taskRepository.countByStatus(TaskStatus.TO_DO)).thenReturn(2);
            when(taskRepository.countByStatus(TaskStatus.IN_PROGRESS)).thenReturn(3);
            when(taskRepository.countByStatus(TaskStatus.DONE)).thenReturn(4);

            TaskSummary summary = taskService.getTaskSummary();

            assertThat(summary.getToDo()).isEqualTo(2);
            assertThat(summary.getInProgress()).isEqualTo(3);
            assertThat(summary.getDone()).isEqualTo(4);
            assertThat(summary.getTotal()).isEqualTo(9);
        }
    }

    @Nested
    class CreateTask {
        @Test
        void createsAndSavesNewTask() {
            when(taskRepository.saveTask(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task result = taskService.createTask(new CreateTaskCommand("New", "Desc", TaskStatus.IN_PROGRESS));

            ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).saveTask(captor.capture());
            Task saved = captor.getValue();
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getTitle()).isEqualTo("New");
            assertThat(saved.getDescription()).isEqualTo("Desc");
            assertThat(saved.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(result).isSameAs(saved);
        }

        @Test
        void defaultsStatusToToDo() {
            when(taskRepository.saveTask(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task result = taskService.createTask(new CreateTaskCommand("New", null, null));

            assertThat(result.getStatus()).isEqualTo(TaskStatus.TO_DO);
        }
    }

    @Nested
    class PatchTask {
        @Test
        void patchesAndSavesExistingTask() {
            when(taskRepository.findTaskById(id)).thenReturn(Optional.of(existingTask));
            when(taskRepository.saveTask(existingTask)).thenReturn(existingTask);

            Task result = taskService.patchTask(id, new PatchTaskCommand("Updated", "", TaskStatus.DONE));

            assertThat(result.getTitle()).isEqualTo("Updated");
            assertThat(result.getDescription()).isNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
            verify(taskRepository).saveTask(existingTask);
        }

        @Test
        void throwsWhenTaskNotFound() {
            when(taskRepository.findTaskById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.patchTask(id, new PatchTaskCommand("x", null, null)))
                    .isInstanceOf(RecordNotFoundException.class);
            verify(taskRepository, never()).saveTask(any());
        }
    }

    @Nested
    class MoveTask {
        @Test
        void movesAndSavesExistingTask() {
            when(taskRepository.findTaskById(id)).thenReturn(Optional.of(existingTask));
            when(taskRepository.saveTask(existingTask)).thenReturn(existingTask);

            Task result = taskService.moveTask(id, new MoveTaskCommand(TaskStatus.IN_PROGRESS));

            assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            verify(taskRepository).saveTask(existingTask);
        }

        @Test
        void throwsWhenTaskNotFound() {
            when(taskRepository.findTaskById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.moveTask(id, new MoveTaskCommand(TaskStatus.DONE)))
                    .isInstanceOf(RecordNotFoundException.class);
            verify(taskRepository, never()).saveTask(any());
        }
    }

    @Nested
    class DeleteTask {
        @Test
        void deletesWhenTaskExists() {
            when(taskRepository.existsById(id)).thenReturn(true);

            taskService.deleteTask(id);

            verify(taskRepository).deleteTask(id);
        }

        @Test
        void throwsWhenTaskDoesNotExist() {
            when(taskRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> taskService.deleteTask(id))
                    .isInstanceOf(RecordNotFoundException.class)
                    .hasMessage("Task [" + id + "] not found");
            verify(taskRepository, never()).deleteTask(any());
        }
    }
}
