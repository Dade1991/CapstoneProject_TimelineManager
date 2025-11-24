package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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
        boolean isOverdue,
        Set<CategoryResponse> categories,
        int position
) {

    // converte la "taskEntity" in "taskResponse"

    public static TaskResponse fromEntity(Task task) {

        Set<CategoryResponse> categoryResponses = task.getCategories() != null ?
                task.getCategories()
                        .stream()
                        .map(CategoryResponse::fromEntity)
                        .collect(Collectors.toSet()) :
                Set.of();

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
                task.isOverdue(),
                categoryResponses,
                task.getPosition()
        );
    }
}