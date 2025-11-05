package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotBlank(message = "'Name' field is mandatory.")
        @Size(min = 2, max = 50, message = "Name should have 2 to 50 characters.")
        String name,

        @NotBlank(message = "'Surname' field is mandatory.")
        @Size(min = 2, max = 50, message = "Surname should have 2 to 50 characters.")
        String surname,

        @NotBlank(message = "'Nickname' field is mandatory.")
        @Size(min = 3, max = 30, message = "Nickname should have 3 to 30 characters.")
        String nickname,

        @NotBlank(message = "'Email' field is mandatory.")
        @Email(message = "The email is not in the correct format.")
        String email,

        @NotBlank(message = "'Password' field is mandatory.")
        @Size(min = 8, max = 100, message = "Password should have 8 to 100 characters.")
        String password
) {
}