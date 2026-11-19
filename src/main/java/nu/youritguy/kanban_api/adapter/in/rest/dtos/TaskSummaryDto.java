package nu.youritguy.kanban_api.adapter.in.rest.dtos;

import lombok.Getter;
import lombok.Setter;
import nu.youritguy.kanban_api.domain.models.TaskSummary;

@Getter
@Setter
public class TaskSummaryDto {
    private int toDo;
    private int inProgress;
    private int done;
    private int total;

    private TaskSummaryDto(int toDo, int inProgress, int done, int total) {
        this.toDo = toDo;
        this.inProgress = inProgress;
        this.done = done;
        this.total = total;
    }

    public static TaskSummaryDto createTaskSummaryDto(TaskSummary taskSummary) {
        return new TaskSummaryDto(taskSummary.getToDo(), taskSummary.getInProgress(), taskSummary.getDone(), taskSummary.getTotal());
    }
}
