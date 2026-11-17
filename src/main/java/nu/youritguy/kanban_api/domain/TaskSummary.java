package nu.youritguy.kanban_api.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskSummary {
    private int toDo;
    private int inProgress;
    private int done;
    private int total;
}
