package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;

import java.time.LocalDate;

public record TaskFilterRequest(

        // paginazione

        Integer pageNumber,
        Integer pageSize,
        String sortBy,
        String sortDirection,

        // FILTRO ricerca

        String search,

        // FILTRI per campo specifico

        Long projectId,
        Long statusId,
        TaskPriorityENUM taskPriority,
        Long assigneeId,

        // FILTRI booleani

        Boolean isCompleted,
        Boolean isExpired,

        // FILTRI per data specifica

        LocalDate createdAt,
        LocalDate createdBefore,
        LocalDate expiryDateBefore,
        LocalDate expiryDateAfter
) {
}