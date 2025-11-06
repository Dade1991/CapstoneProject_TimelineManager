package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Signup_DTO_RequestsAndResponses;

public record SignupRequest(
        String name,
        String surname,
        String nickname,
        String email,
        String password
) {
}