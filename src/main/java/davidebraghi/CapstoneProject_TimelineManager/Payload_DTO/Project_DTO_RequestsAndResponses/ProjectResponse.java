package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.entities.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long projectId,
        String projectName,
        String projectDescription,
        LocalDateTime creationDate,
        LocalDate expiryDate,
        Long creatorId,
        int memberCount,
        int taskCount,
        boolean isOverdue
) {

    // converte la "projectEntity" in "projectResponse"

    public static ProjectResponse fromEntity(Project project) {
        return new ProjectResponse(
                project.getProjectId(),
                project.getProjectName(),
                project.getProjectDescription(),
                project.getCreationDate(),
                project.getExpiryDate(),
                project.getCreator().getUserId(),
                project.getProjectMembers() != null ? project.getProjectMembers().size() : 0,
                project.getTaskCount(),
                project.isOverdue()
        );
    }
}