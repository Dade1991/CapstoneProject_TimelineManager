package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "Category is mandatory.")
        @Size(min = 2, max = 50, message = "Category should have 2 to 50 characters.")
        String categoryName,
        String categoryColor,
        Long projectId
) {
}
