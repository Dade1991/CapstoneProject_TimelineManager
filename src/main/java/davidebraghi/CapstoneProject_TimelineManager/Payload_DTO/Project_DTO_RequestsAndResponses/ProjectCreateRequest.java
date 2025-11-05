package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectCreateRequest(
        @Size(min = 3, max = 100, message = "Il nome deve avere 3-100 caratteri")
        String projectName,

        @Size(max = 500, message = "La descrizione deve avere massimo 500 caratteri")
        String projectDescription,

        @Future(message = "La data di scadenza deve essere nel futuro")
        LocalDate expiryDate
) {
}