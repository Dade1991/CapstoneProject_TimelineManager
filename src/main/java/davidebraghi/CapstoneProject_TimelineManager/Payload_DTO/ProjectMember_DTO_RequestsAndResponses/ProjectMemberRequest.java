package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectMember_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;

public record ProjectMemberRequest(
        Long userId,
        Long projectId,
        RoleNameENUM role
) {
}