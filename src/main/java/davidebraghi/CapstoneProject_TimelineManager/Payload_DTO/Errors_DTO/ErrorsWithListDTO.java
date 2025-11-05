package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Errors_DTO;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorsWithListDTO(String message,
                                LocalDateTime timestamp,
                                List<String> errorsList) {
}