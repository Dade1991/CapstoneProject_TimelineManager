package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Member_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;

public record MemberRequest(
        Long userId,
        RoleNameENUM role
) {
}