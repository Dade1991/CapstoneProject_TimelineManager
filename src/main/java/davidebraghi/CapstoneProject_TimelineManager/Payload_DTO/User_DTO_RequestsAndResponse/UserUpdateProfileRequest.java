package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateProfileRequest(
        @Size(min = 2, max = 50, message = "Name should have 2 to 50 characters.")
        String name,

        @Size(min = 2, max = 50, message = "Surname should have 2 to 50 characters.")
        String surname,

        @Size(min = 3, max = 30, message = "Nickname should have 3 to 30 characters.")
        String nickname,

        @Email(message = "The email is not in the correct format.")
        String email
) {
}
