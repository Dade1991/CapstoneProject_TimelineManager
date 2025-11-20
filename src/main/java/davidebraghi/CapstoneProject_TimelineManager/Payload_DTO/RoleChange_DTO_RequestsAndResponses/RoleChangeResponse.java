package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses;

public record RoleChangeResponse(
        Long userId,
        String nickname,
        String newRole,
        String statusMessage
) {
}