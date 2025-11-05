package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserResponse;

public record AuthResponse(String accessToken,
                           String tokenType,
                           UserResponse user) {
}