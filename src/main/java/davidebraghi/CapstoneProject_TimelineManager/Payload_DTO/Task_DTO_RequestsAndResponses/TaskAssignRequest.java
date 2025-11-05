package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses;

import jakarta.validation.constraints.NotNull;

public record TaskAssignRequest(
        @NotNull(message = "User ID is mandatory.")
        Long userId
) {
}