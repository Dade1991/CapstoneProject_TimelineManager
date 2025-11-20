package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Member_DTO_RequestsAndResponses;


import davidebraghi.CapstoneProject_TimelineManager.entities.Project_User_Role;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;

public record MemberResponse(
        Long userId,
        String userFullName,
        String email,
        RoleNameENUM role
) {
    public static MemberResponse fromEntity(Project_User_Role projectUserRole) {
        return new MemberResponse(
                projectUserRole.getUser().getUserId(),
                projectUserRole.getUser().getName() + " " + projectUserRole.getUser().getSurname(),
                projectUserRole.getUser().getEmail(),
                projectUserRole.getRole().getRoleName()
        );
    }
}