package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import jakarta.validation.constraints.NotNull;

public record ProjectAddMemberRequest(
        @NotNull(message = "User ID is mandatory.")
        Long userId,

        @NotNull(message = "Role is mandatory.")
        RoleNameENUM roleName
) {
}