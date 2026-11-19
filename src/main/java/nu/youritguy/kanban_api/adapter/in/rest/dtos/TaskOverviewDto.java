package nu.youritguy.kanban_api.adapter.in.rest.dtos;

import lombok.Getter;
import lombok.Setter;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class TaskOverviewDto {
    private List<TaskDto> toDo;
    private List<TaskDto> inProgress;
    private List<TaskDto> done;

    public static TaskOverviewDto createTaskOverview(TaskOverview taskOverview) {
        return new TaskOverviewDto(
                toDtos(taskOverview.getToDo()),
                toDtos(taskOverview.getInProgress()),
                toDtos(taskOverview.getDone())
        );
    }

    private TaskOverviewDto(List<TaskDto> toDo, List<TaskDto> inProgress, List<TaskDto> done) {
        this.toDo = toDo;
        this.inProgress = inProgress;
        this.done = done;
    }

    private static List<TaskDto> toDtos(List<Task> tasks) {
        return tasks.stream()
                .filter(Objects::nonNull)
                .map(TaskDto::createTaskDto)
                .toList();
    }
}
