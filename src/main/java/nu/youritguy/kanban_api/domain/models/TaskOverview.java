package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskOverview {
    private List<Task> toDo;
    private List<Task> inProgress;
    private List<Task> done;
}
