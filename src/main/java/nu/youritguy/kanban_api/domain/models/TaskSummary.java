package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSummary {
    private int toDo;
    private int inProgress;
    private int done;
    private int total;

    private TaskSummary(int toDo, int inProgress, int done, int total) {
        this.toDo = toDo;
        this.inProgress = inProgress;
        this.done = done;
        this.total = total;
    }

    public static TaskSummary createTaskSummary(int toDo, int inProgress, int done, int total) {
        return new TaskSummary(toDo, inProgress, done, total);
    }
}
