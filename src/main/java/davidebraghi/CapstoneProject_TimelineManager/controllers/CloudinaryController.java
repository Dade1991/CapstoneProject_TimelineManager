package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Cloudinary_DTO_RequestsAndResponses.AvatarUrlUploadResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/users/{userId}/avatar")
public class CloudinaryController {

    @Autowired
    private UserService userService;

    public CloudinaryController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AvatarUrlUploadResponse> uploadAvatar(@PathVariable Long userId,
                                                                @RequestParam("file") MultipartFile file) {
        try {
            User updatedUser = userService.uploadAvatarProfilePic(userId, file);
            String avatarUrl = updatedUser.getAvatarUrl();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AvatarUrlUploadResponse(avatarUrl, "Profile pic update!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AvatarUrlUploadResponse(null, "Upload fallito: " + e.getMessage()));
        }
    }
}
