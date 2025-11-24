package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectMember_DTO_RequestsAndResponses;


import davidebraghi.CapstoneProject_TimelineManager.entities.ProjectMember;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;

public record ProjectMemberResponse(
        Long userId,
        String userFullName,
        String email,
        String avatarUrl,
        RoleNameENUM role,
        int taskCount
) {
    public static ProjectMemberResponse from(ProjectMember member, int taskCount) {
        return new ProjectMemberResponse(
                member.getUser().getUserId(),
                member.getUser().getName() + " " + member.getUser().getSurname(),
                member.getUser().getEmail(),
                member.getUser().getAvatarUrl(),
                member.getRole().getRoleName(),
                taskCount
        );
    }
}