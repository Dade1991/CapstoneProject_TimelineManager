package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
        @NotBlank(message = "Category name is mandatory.")
        @Size(min = 2, max = 50, message = "Category name should have 2 to 50 characters.")
        String categoryName
) {
}
