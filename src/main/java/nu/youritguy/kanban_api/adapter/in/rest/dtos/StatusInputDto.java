package nu.youritguy.kanban_api.adapter.in.rest.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusInputDto {
    @NotBlank(message = "Status should not be empty")
    @Schema(allowableValues = {"TO_DO", "IN_PROGRESS", "DONE", "To Do", "In Progress", "Done"})
    private String status;
}
