package nu.youritguy.kanban_api.domain.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaskOverviewTest {

    @Test
    void newOverviewHasEmptyLists() {
        TaskOverview overview = new TaskOverview();

        assertThat(overview.getToDo()).isEmpty();
        assertThat(overview.getInProgress()).isEmpty();
        assertThat(overview.getDone()).isEmpty();
    }

    @Test
    void createTaskOverviewGroupsTasksByStatus() {
        Task todo1 = Task.createTask("todo1", null, TaskStatus.TO_DO);
        Task todo2 = Task.createTask("todo2", null, TaskStatus.TO_DO);
        Task inProgress = Task.createTask("busy", null, TaskStatus.IN_PROGRESS);
        Task done = Task.createTask("done", null, TaskStatus.DONE);

        TaskOverview overview = TaskOverview.createTaskOverview(List.of(todo1, inProgress, todo2, done));

        assertThat(overview.getToDo()).containsExactly(todo1, todo2);
        assertThat(overview.getInProgress()).containsExactly(inProgress);
        assertThat(overview.getDone()).containsExactly(done);
    }

    @Test
    void createTaskOverviewWithEmptyListReturnsEmptyOverview() {
        TaskOverview overview = TaskOverview.createTaskOverview(List.of());

        assertThat(overview.getToDo()).isEmpty();
        assertThat(overview.getInProgress()).isEmpty();
        assertThat(overview.getDone()).isEmpty();
    }

    @Test
    void settersReplaceLists() {
        TaskOverview overview = new TaskOverview();
        Task task = Task.createTask("t", null, null);

        overview.setToDo(List.of(task));
        overview.setInProgress(List.of(task));
        overview.setDone(List.of(task));

        assertThat(overview.getToDo()).containsExactly(task);
        assertThat(overview.getInProgress()).containsExactly(task);
        assertThat(overview.getDone()).containsExactly(task);
    }
}
