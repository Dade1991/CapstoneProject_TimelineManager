package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import jakarta.validation.constraints.Size;

public record UserUpdateProfileRequest(
        @Size(min = 2, max = 50, message = "Name should have 2 to 50 characters.")
        String name,

        @Size(min = 2, max = 50, message = "Surname should have 2 to 50 characters.")
        String surname,

        String profilePicUrl
) {
}
