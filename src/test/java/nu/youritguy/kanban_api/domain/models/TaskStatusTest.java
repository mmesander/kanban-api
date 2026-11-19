package nu.youritguy.kanban_api.domain.models;

import nu.youritguy.kanban_api.domain.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskStatusTest {

    @Test
    void labelsAreCorrect() {
        assertThat(TaskStatus.TO_DO.getLabel()).isEqualTo("To Do");
        assertThat(TaskStatus.IN_PROGRESS.getLabel()).isEqualTo("In Progress");
        assertThat(TaskStatus.DONE.getLabel()).isEqualTo("Done");
    }

    @ParameterizedTest
    @CsvSource({
            "TO_DO, TO_DO",
            "to_do, TO_DO",
            "To Do, TO_DO",
            "to do, TO_DO",
            "IN_PROGRESS, IN_PROGRESS",
            "in_progress, IN_PROGRESS",
            "In Progress, IN_PROGRESS",
            "IN PROGRESS, IN_PROGRESS",
            "DONE, DONE",
            "done, DONE",
            "Done, DONE"
    })
    void retrievesEnumFromNameOrLabelCaseInsensitive(String input, TaskStatus expected) {
        assertThat(TaskStatus.retrieveEnumFromInputStatus(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN", "todo", "In-Progress", "finished"})
    @NullAndEmptySource
    void throwsInvalidInputExceptionForUnknownStatus(String input) {
        assertThatThrownBy(() -> TaskStatus.retrieveEnumFromInputStatus(input))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Status [" + input + "] unknown. Allowed values: "
                        + "TO_DO/To Do, IN_PROGRESS/In Progress, DONE/Done");
    }
}
