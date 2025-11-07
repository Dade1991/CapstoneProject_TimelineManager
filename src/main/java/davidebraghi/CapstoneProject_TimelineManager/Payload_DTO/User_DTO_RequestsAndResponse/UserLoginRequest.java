package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank(message = "Email field is mandatory.")
        @Email(message = "The email is not in the correct format.")
        String email,

        @NotBlank(message = "Password field is mandatory.")
        String password
) {
}