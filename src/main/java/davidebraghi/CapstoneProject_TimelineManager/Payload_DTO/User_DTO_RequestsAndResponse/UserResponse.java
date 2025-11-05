package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;

import java.time.LocalDate;

public record UserResponse(Long userId,
                           String name,
                           String surname,
                           String nickname,
                           String profilePicUrl,
                           String email,
                           LocalDate creationDate
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getSurname(),
                user.getNickname(),
                user.getProfilePicUrl(),
                user.getEmail(),
                user.getCreationDate()
        );
    }
}