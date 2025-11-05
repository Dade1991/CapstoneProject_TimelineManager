package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import jakarta.validation.constraints.NotNull;

public record ProjectAddMemberRequest(
        @NotNull(message = "L'ID dell'utente è obbligatorio")
        Long userId,

        @NotNull(message = "Il ruolo è obbligatorio")
        RoleNameENUM roleName
) {
}