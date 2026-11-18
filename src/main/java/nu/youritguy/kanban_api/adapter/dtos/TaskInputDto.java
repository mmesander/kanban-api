package nu.youritguy.kanban_api.adapter.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "New Task")
public class TaskInputDto {
    @NotBlank(message = "Title should not be empty")
    @Size(max = 100, message = "Title can't exceed 100 characters")
    private String title;

    @Size(max = 1000, message = "Description can't exceed 1000 characters")
    private String description;

    @Schema(allowableValues = {"TO_DO", "IN_PROGRESS", "DONE", "To Do", "In Progress", "Done"})
    private String status;
}
