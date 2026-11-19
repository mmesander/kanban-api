package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;
import nu.youritguy.kanban_api.domain.exceptions.InvalidInputException;

import java.util.Arrays;
import java.util.stream.Collectors;

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
                        "Status [%s] unknown. Allowed values: %s", status, allowedValues()
                )));
    }

    private static String allowedValues() {
        return Arrays.stream(values())
                .map(taskStatus -> taskStatus.name() + "/" + taskStatus.label)
                .collect(Collectors.joining(", "));
    }
}
