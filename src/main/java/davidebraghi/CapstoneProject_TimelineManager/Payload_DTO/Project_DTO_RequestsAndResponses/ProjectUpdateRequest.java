package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectUpdateRequest(
        @Size(min = 3, max = 100, message = "Project name should have 3 to 100 characters.")
        String projectName,

        @Size(max = 500, message = "Description should have max 500 characters.")
        String projectDescription,

        @Future(message = "Expiry date shall be in future.")
        LocalDate expiryDate
) {
}