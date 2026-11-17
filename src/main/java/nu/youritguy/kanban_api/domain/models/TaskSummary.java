package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskSummary {
    List<Task> toDo;
    List<Task> inProgress;
    List<Task> done;
}
