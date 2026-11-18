package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import nu.youritguy.kanban_api.domain.exceptions.InvalidInputException;

import java.util.Arrays;

@Getter
public enum TaskStatus {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public static TaskStatus retrieveEnumFromInputStatus(String status) {
        return Arrays.stream(values())
                .filter(taskStatus -> taskStatus.name().equalsIgnoreCase(status) || taskStatus.label.equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException(String.format(
                        "Status [%s] unknown. Allowed values: TO_DO, To Do, IN_PROGRESS, In Progress and DONE"
                )));
    }
}
