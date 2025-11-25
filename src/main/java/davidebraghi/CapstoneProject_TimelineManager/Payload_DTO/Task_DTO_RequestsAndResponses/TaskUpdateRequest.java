package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record TaskUpdateRequest(
        @Size(min = 3, max = 100, message = "Task title Update Ta 3 to 100 characters.")
        String taskTitle,
        
        String taskDescription,

        TaskPriorityENUM taskPriority,

        Long taskStatusId,

        @Future(message = "Expiry date shall be in future.")
        LocalDate taskExpiryDate,

        Set<Long> categoryIds,

        Integer position
) {
}