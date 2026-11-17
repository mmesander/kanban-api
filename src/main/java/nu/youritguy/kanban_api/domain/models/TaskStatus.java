package nu.youritguy.kanban_api.domain.models;

import lombok.Getter;

@Getter
public enum TaskStatus {
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }
}
