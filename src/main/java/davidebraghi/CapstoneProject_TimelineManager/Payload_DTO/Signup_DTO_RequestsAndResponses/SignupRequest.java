package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Signup_DTO_RequestsAndResponses;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Name is mandatory.")
        String name,
        @NotBlank(message = "Surname is mandatory.")
        String surname,
        @NotBlank(message = "Nickname is mandatory.")
        String nickname,
        @NotBlank(message = "Email is mandatory.")
        @Email(message = "Email must be valid.")
        String email,
        @NotBlank(message = "Password is mandatory.")
        @Size(min = 8, message = "Password must be at least 8 characters.")
        String password
) {
}