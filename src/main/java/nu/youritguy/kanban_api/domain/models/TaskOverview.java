package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskOverview {
    private List<Task> toDo = new ArrayList<>();
    private List<Task> inProgress = new ArrayList<>();
    private List<Task> done = new ArrayList<>();

    public TaskOverview() {
    }

    public static TaskOverview createTaskOverview(List<Task> allTasks) {
        TaskOverview taskOverview = new TaskOverview();

        for (Task task : allTasks) {
            switch (task.getStatus()) {
                case TO_DO:
                    taskOverview.getToDo().add(task);
                    break;
                case IN_PROGRESS:
                    taskOverview.getInProgress().add(task);
                    break;
                case DONE:
                    taskOverview.getDone().add(task);
                    break;
            }
        }
        return taskOverview;
    }
}
