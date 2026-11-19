package nu.youritguy.kanban_api.adapter.in.rest.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InputDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    private static <T> Set<String> messages(T dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Nested
    class TaskInput {
        private TaskInputDto dto(String title, String description, String status) {
            TaskInputDto dto = new TaskInputDto();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setStatus(status);
            return dto;
        }

        @Test
        void validDtoHasNoViolations() {
            TaskInputDto dto = dto("Title", "Desc", "TO_DO");

            assertThat(validator.validate(dto)).isEmpty();
            assertThat(dto.getTitle()).isEqualTo("Title");
            assertThat(dto.getDescription()).isEqualTo("Desc");
            assertThat(dto.getStatus()).isEqualTo("TO_DO");
        }

        @Test
        void onlyTitleIsRequired() {
            assertThat(validator.validate(dto("Title", null, null))).isEmpty();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void blankTitleIsInvalid(String title) {
            assertThat(messages(dto(title, null, null))).contains("Title should not be empty");
        }

        @Test
        void titleOf100CharsIsValidButNot101() {
            assertThat(validator.validate(dto("a".repeat(100), null, null))).isEmpty();
            assertThat(messages(dto("a".repeat(101), null, null))).containsExactly("Title can't exceed 100 characters");
        }

        @Test
        void descriptionOf500CharsIsValidButNot501() {
            assertThat(validator.validate(dto("T", "a".repeat(500), null))).isEmpty();
            assertThat(messages(dto("T", "a".repeat(501), null))).containsExactly("Description can't exceed 500 characters");
        }
    }

    @Nested
    class TaskPatchInput {
        private TaskPatchInputDto dto(String title, String description, String status) {
            TaskPatchInputDto dto = new TaskPatchInputDto();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setStatus(status);
            return dto;
        }

        @Test
        void allFieldsOptional() {
            TaskPatchInputDto dto = dto(null, null, null);

            assertThat(validator.validate(dto)).isEmpty();
            assertThat(dto.getTitle()).isNull();
            assertThat(dto.getDescription()).isNull();
            assertThat(dto.getStatus()).isNull();
        }

        @Test
        void emptyTitleIsInvalid() {
            assertThat(messages(dto("", null, null))).containsExactly("Title must be between 1 and 100 characters");
        }

        @Test
        void titleOf101CharsIsInvalid() {
            assertThat(validator.validate(dto("a".repeat(100), null, null))).isEmpty();
            assertThat(messages(dto("a".repeat(101), null, null))).containsExactly("Title must be between 1 and 100 characters");
        }

        @Test
        void descriptionOf501CharsIsInvalid() {
            assertThat(validator.validate(dto(null, "a".repeat(500), null))).isEmpty();
            assertThat(messages(dto(null, "a".repeat(501), null))).containsExactly("Description can't exceed 500 characters");
        }
    }

    @Nested
    class StatusInput {
        @Test
        void validStatus() {
            StatusInputDto dto = new StatusInputDto();
            dto.setStatus("DONE");

            assertThat(validator.validate(dto)).isEmpty();
            assertThat(dto.getStatus()).isEqualTo("DONE");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void blankStatusIsInvalid(String status) {
            StatusInputDto dto = new StatusInputDto();
            dto.setStatus(status);

            assertThat(messages(dto)).containsExactly("Status should not be empty");
        }
    }
}
