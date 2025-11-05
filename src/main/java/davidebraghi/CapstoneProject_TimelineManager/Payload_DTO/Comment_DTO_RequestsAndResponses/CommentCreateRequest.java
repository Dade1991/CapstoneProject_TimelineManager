package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Comment_DTO_RequestsAndResponses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank(message = "The comment text is mandatory.")
        @Size(min = 1, max = 500, message = "Comment text should have 1 min to max 500 characters.")
        String commentText,

        @NotNull(message = "Task ID is mandatory.")
        Long taskId
) {
}
