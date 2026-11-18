package nu.youritguy.kanban_api.domain.exceptions;

import java.io.Serial;

public class InvalidInputException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidInputException() {
        super();
    }

    public InvalidInputException(String message) {
        super(message);
    }
}
