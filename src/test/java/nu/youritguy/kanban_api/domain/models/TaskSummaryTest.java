package nu.youritguy.kanban_api.domain.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskSummaryTest {

    @Test
    void createTaskSummarySetsAllCounts() {
        TaskSummary summary = TaskSummary.createTaskSummary(1, 2, 3, 6);

        assertThat(summary.getToDo()).isEqualTo(1);
        assertThat(summary.getInProgress()).isEqualTo(2);
        assertThat(summary.getDone()).isEqualTo(3);
        assertThat(summary.getTotal()).isEqualTo(6);
    }

    @Test
    void settersUpdateCounts() {
        TaskSummary summary = TaskSummary.createTaskSummary(0, 0, 0, 0);

        summary.setToDo(4);
        summary.setInProgress(5);
        summary.setDone(6);
        summary.setTotal(15);

        assertThat(summary.getToDo()).isEqualTo(4);
        assertThat(summary.getInProgress()).isEqualTo(5);
        assertThat(summary.getDone()).isEqualTo(6);
        assertThat(summary.getTotal()).isEqualTo(15);
    }
}
