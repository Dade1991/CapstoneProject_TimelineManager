package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserChangePasswordRequest(
        @NotBlank(message = "Old password is required.")
        String oldPassword,

        @Size(min = 8, max = 100, message = "New password should have 8 to 100 characters.")
        String newPassword
) {
}
