package nu.youritguy.kanban_api.domain.exceptions;

import java.io.Serial;
import java.util.UUID;

public class RecordNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RecordNotFoundException() {
        super();
    }

    public RecordNotFoundException(UUID id) {
        super(String.format("Task [%s] not found", id));
    }
}
