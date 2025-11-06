package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;

import java.time.LocalDate;

public record UserResponse(Long userId,
                           String name,
                           String surname,
                           String nickname,
                           String email,
                           LocalDate creationDate,
                           String avatarUrl
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getSurname(),
                user.getNickname(),
                user.getEmail(),
                user.getCreationDate(),
                user.getAvatarUrl()
        );
    }
}