package nu.youritguy.kanban_api.domain.models;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    private static Task existingTask() {
        Instant past = Instant.parse("2024-01-01T10:00:00Z");
        return new Task(UUID.randomUUID(), "Old title", "Old description", TaskStatus.TO_DO, past, past);
    }

    @Nested
    class Constructors {
        @Test
        void noArgsConstructorLeavesAllFieldsNull() {
            Task task = new Task();

            assertThat(task.getId()).isNull();
            assertThat(task.getTitle()).isNull();
            assertThat(task.getDescription()).isNull();
            assertThat(task.getStatus()).isNull();
            assertThat(task.getCreatedAt()).isNull();
            assertThat(task.getUpdatedAt()).isNull();
        }

        @Test
        void allArgsConstructorSetsAllFields() {
            UUID id = UUID.randomUUID();
            Instant created = Instant.parse("2024-01-01T10:00:00Z");
            Instant updated = Instant.parse("2024-01-02T10:00:00Z");

            Task task = new Task(id, "Title", "Desc", TaskStatus.DONE, created, updated);

            assertThat(task.getId()).isEqualTo(id);
            assertThat(task.getTitle()).isEqualTo("Title");
            assertThat(task.getDescription()).isEqualTo("Desc");
            assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
            assertThat(task.getCreatedAt()).isEqualTo(created);
            assertThat(task.getUpdatedAt()).isEqualTo(updated);
        }
    }

    @Nested
    class CreateTask {
        @Test
        void createsTaskWithGeneratedIdAndTimestamps() {
            Instant before = Instant.now();

            Task task = Task.createTask("Title", "Desc", TaskStatus.IN_PROGRESS);

            assertThat(task.getId()).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Title");
            assertThat(task.getDescription()).isEqualTo("Desc");
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(task.getCreatedAt()).isBetween(before, Instant.now());
            assertThat(task.getUpdatedAt()).isEqualTo(task.getCreatedAt());
        }

        @Test
        void defaultsStatusToToDoWhenNull() {
            Task task = Task.createTask("Title", null, null);

            assertThat(task.getStatus()).isEqualTo(TaskStatus.TO_DO);
            assertThat(task.getDescription()).isNull();
        }

        @Test
        void generatesUniqueIds() {
            Task first = Task.createTask("A", null, null);
            Task second = Task.createTask("B", null, null);

            assertThat(first.getId()).isNotEqualTo(second.getId());
        }
    }

    @Nested
    class PatchTask {
        @Test
        void updatesAllFieldsWhenProvided() {
            Task task = existingTask();
            Instant oldUpdatedAt = task.getUpdatedAt();
            Instant oldCreatedAt = task.getCreatedAt();

            task.patchTask("New title", "New description", TaskStatus.DONE);

            assertThat(task.getTitle()).isEqualTo("New title");
            assertThat(task.getDescription()).isEqualTo("New description");
            assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
            assertThat(task.getUpdatedAt()).isAfter(oldUpdatedAt);
            assertThat(task.getCreatedAt()).isEqualTo(oldCreatedAt);
        }

        @Test
        void keepsAllFieldsWhenNullsProvidedButUpdatesTimestamp() {
            Task task = existingTask();
            Instant oldUpdatedAt = task.getUpdatedAt();

            task.patchTask(null, null, null);

            assertThat(task.getTitle()).isEqualTo("Old title");
            assertThat(task.getDescription()).isEqualTo("Old description");
            assertThat(task.getStatus()).isEqualTo(TaskStatus.TO_DO);
            assertThat(task.getUpdatedAt()).isAfter(oldUpdatedAt);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        void keepsTitleWhenBlank(String blankTitle) {
            Task task = existingTask();

            task.patchTask(blankTitle, null, null);

            assertThat(task.getTitle()).isEqualTo("Old title");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        void clearsDescriptionWhenBlank(String blankDescription) {
            Task task = existingTask();

            task.patchTask(null, blankDescription, null);

            assertThat(task.getDescription()).isNull();
        }
    }

    @Nested
    class MoveTask {
        @Test
        void changesStatusAndUpdatesTimestamp() {
            Task task = existingTask();
            Instant oldUpdatedAt = task.getUpdatedAt();

            task.moveTask(TaskStatus.IN_PROGRESS);

            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(task.getUpdatedAt()).isAfter(oldUpdatedAt);
            assertThat(task.getTitle()).isEqualTo("Old title");
            assertThat(task.getDescription()).isEqualTo("Old description");
        }
    }

    @Test
    void settersUpdateFields() {
        Task task = new Task();
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        task.setId(id);
        task.setTitle("T");
        task.setDescription("D");
        task.setStatus(TaskStatus.DONE);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getTitle()).isEqualTo("T");
        assertThat(task.getDescription()).isEqualTo("D");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(task.getCreatedAt()).isEqualTo(now);
        assertThat(task.getUpdatedAt()).isEqualTo(now);
    }
}
