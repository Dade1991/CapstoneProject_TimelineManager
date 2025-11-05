package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.entities.Project;

import java.time.LocalDate;

public record ProjectResponse(
        Long projectId,
        String projectName,
        String projectDescription,
        LocalDate creationDate,
        LocalDate expiryDate,
        Long creatorId,
        int memberCount,
        int taskCount,
        boolean isOverdue
) {
    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getProjectId(),
                project.getProjectName(),
                project.getProjectDescription(),
                project.getCreationDate(),
                project.getExpiryDate(),
                project.getCreator().getUserId(),
                project.getProjectUserRoles() != null ? project.getProjectUserRoles().size() : 0,
                project.getTaskCount(),
                project.isOverdue()
        );
    }
}