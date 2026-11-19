package nu.youritguy.kanban_api.adapter.in.rest.controllers;

import nu.youritguy.kanban_api.domain.exceptions.BadRequestException;
import nu.youritguy.kanban_api.domain.exceptions.InvalidInputException;
import nu.youritguy.kanban_api.domain.exceptions.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionControllerTest {

    private final ExceptionController controller = new ExceptionController();

    @Test
    void invalidInputReturns400WithMessage() {
        ResponseEntity<String> response = controller.handleInvalidInput(new InvalidInputException("invalid"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("invalid");
    }

    @Test
    void badRequestReturns400WithMessage() {
        ResponseEntity<String> response = controller.handleBadRequest(new BadRequestException("bad"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("bad");
    }

    @Test
    void recordNotFoundReturns404WithMessage() {
        UUID id = UUID.randomUUID();

        ResponseEntity<String> response = controller.handleRecordNotFound(new RecordNotFoundException(id));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Task [" + id + "] not found");
    }
}
