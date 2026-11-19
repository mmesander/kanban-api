package nu.youritguy.kanban_api.adapter.in.rest.dtos;

import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import nu.youritguy.kanban_api.domain.models.TaskSummary;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMappingTest {

    private final Instant created = Instant.parse("2024-01-01T10:00:00Z");
    private final Instant updated = Instant.parse("2024-01-02T10:00:00Z");

    private Task task(TaskStatus status) {
        return new Task(UUID.randomUUID(), "Title", "Desc", status, created, updated);
    }

    @Test
    void taskDtoCopiesAllFieldsFromTask() {
        Task task = task(TaskStatus.IN_PROGRESS);

        TaskDto dto = TaskDto.createTaskDto(task);

        assertThat(dto.getId()).isEqualTo(task.getId());
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(dto.getCreatedAt()).isEqualTo(created);
        assertThat(dto.getUpdatedAt()).isEqualTo(updated);
    }

    @Test
    void taskDtoSetters() {
        TaskDto dto = TaskDto.createTaskDto(task(TaskStatus.TO_DO));
        UUID newId = UUID.randomUUID();

        dto.setId(newId);
        dto.setTitle("New");
        dto.setDescription(null);
        dto.setStatus(TaskStatus.DONE);
        dto.setCreatedAt(updated);
        dto.setUpdatedAt(created);

        assertThat(dto.getId()).isEqualTo(newId);
        assertThat(dto.getTitle()).isEqualTo("New");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(dto.getCreatedAt()).isEqualTo(updated);
        assertThat(dto.getUpdatedAt()).isEqualTo(created);
    }

    @Test
    void taskOverviewDtoMapsAllListsAndSkipsNulls() {
        Task todo = task(TaskStatus.TO_DO);
        Task inProgress = task(TaskStatus.IN_PROGRESS);
        Task done = task(TaskStatus.DONE);
        TaskOverview overview = new TaskOverview();
        List<Task> toDoWithNull = new ArrayList<>();
        toDoWithNull.add(todo);
        toDoWithNull.add(null);
        overview.setToDo(toDoWithNull);
        overview.setInProgress(List.of(inProgress));
        overview.setDone(List.of(done));

        TaskOverviewDto dto = TaskOverviewDto.createTaskOverview(overview);

        assertThat(dto.getToDo()).extracting(TaskDto::getId).containsExactly(todo.getId());
        assertThat(dto.getInProgress()).extracting(TaskDto::getId).containsExactly(inProgress.getId());
        assertThat(dto.getDone()).extracting(TaskDto::getId).containsExactly(done.getId());
    }

    @Test
    void taskOverviewDtoWithEmptyOverview() {
        TaskOverviewDto dto = TaskOverviewDto.createTaskOverview(new TaskOverview());

        assertThat(dto.getToDo()).isEmpty();
        assertThat(dto.getInProgress()).isEmpty();
        assertThat(dto.getDone()).isEmpty();

        dto.setToDo(List.of());
        dto.setInProgress(null);
        dto.setDone(null);
        assertThat(dto.getInProgress()).isNull();
        assertThat(dto.getDone()).isNull();
    }

    @Test
    void taskSummaryDtoCopiesAllCounts() {
        TaskSummaryDto dto = TaskSummaryDto.createTaskSummaryDto(TaskSummary.createTaskSummary(1, 2, 3, 6));

        assertThat(dto.getToDo()).isEqualTo(1);
        assertThat(dto.getInProgress()).isEqualTo(2);
        assertThat(dto.getDone()).isEqualTo(3);
        assertThat(dto.getTotal()).isEqualTo(6);

        dto.setToDo(10);
        dto.setInProgress(20);
        dto.setDone(30);
        dto.setTotal(60);
        assertThat(dto.getToDo()).isEqualTo(10);
        assertThat(dto.getInProgress()).isEqualTo(20);
        assertThat(dto.getDone()).isEqualTo(30);
        assertThat(dto.getTotal()).isEqualTo(60);
    }
}
