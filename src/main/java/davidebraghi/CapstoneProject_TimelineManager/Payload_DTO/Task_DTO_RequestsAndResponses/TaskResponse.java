package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;

import java.time.LocalDate;

public record TaskResponse(
        Long taskId,
        String taskTitle,
        String taskDescription,
        TaskPriorityENUM taskPriority,
        String taskStatus,
        Long projectId,
        String projectName,
        Long creatorId,
        String creatorName,
        Long lastModifiedById,
        LocalDate createdAt,
        LocalDate updatedAt,
        LocalDate completedAt,
        LocalDate taskExpiryDate,
        int assigneeCount,
        int commentCount,
        boolean isCompleted,
        boolean isOverdue
) {

    // converte la "taskEntity" in "taskResponse"

    public static TaskResponse fromEntity(Task task) {
        return new TaskResponse(
                task.getTaskId(),
                task.getTaskTitle(),
                task.getTaskDescription(),
                task.getTaskPriority(),
                task.getStatus() != null ? task.getStatus().getStatusName().toString() : "UNKNOWN",
                task.getProject().getProjectId(),
                task.getProject().getProjectName(),
                task.getCreator().getUserId(),
                task.getCreator().getFullName(),
                task.getLastModifiedBy() != null ? task.getLastModifiedBy().getUserId() : null,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCompletedAt(),
                task.getTaskExpiryDate(),
                task.getAssigneeCount(),
                task.getCommentCount(),
                task.isCompleted(),
                task.isOverdue()
        );
    }
}