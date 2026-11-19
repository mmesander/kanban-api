package nu.youritguy.kanban_api.domain.exceptions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void badRequestExceptionConstructors() {
        assertThat(new BadRequestException().getMessage()).isNull();
        assertThat(new BadRequestException("bad")).hasMessage("bad").isInstanceOf(RuntimeException.class);
    }

    @Test
    void invalidInputExceptionConstructors() {
        assertThat(new InvalidInputException().getMessage()).isNull();
        assertThat(new InvalidInputException("invalid")).hasMessage("invalid").isInstanceOf(RuntimeException.class);
    }

    @Test
    void recordNotFoundExceptionConstructors() {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");

        assertThat(new RecordNotFoundException().getMessage()).isNull();
        assertThat(new RecordNotFoundException(id))
                .hasMessage("Task [11111111-2222-3333-4444-555555555555] not found")
                .isInstanceOf(RuntimeException.class);
    }
}
