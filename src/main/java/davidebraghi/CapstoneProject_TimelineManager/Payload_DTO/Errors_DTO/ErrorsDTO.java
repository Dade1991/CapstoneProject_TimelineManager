package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Errors_DTO;

import java.time.LocalDateTime;

public record ErrorsDTO(String message,
                        LocalDateTime timestamp) {
}