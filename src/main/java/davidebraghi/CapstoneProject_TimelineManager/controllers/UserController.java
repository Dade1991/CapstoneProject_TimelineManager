package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserChangePasswordRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserUpdateProfileRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectService;
import davidebraghi.CapstoneProject_TimelineManager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    // GET - FIND_BY_ID (profilo user loggato) - http://localhost:3001/api/users/me


    @GetMapping("/profile")
    public UserResponse getPersonalProfile(@AuthenticationPrincipal User user) {
        System.out.println("[UserController] getPersonalProfile called for userId: " + user.getUserId());
        return UserResponse.fromEntity(user);
    }

    // GET - FIND_BY_ID - http://localhost:3001/api/users/id/{userId}

    @GetMapping("/id/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        return UserResponse.fromEntity(user);
    }

    // GET - FIND_BY_NICKNAME - http://localhost:3001/api/users/nickname/{nickname}

    @GetMapping("/nickname/{nickname}")
    public UserResponse getUserByNickname(@PathVariable String nickname) {
        User user = userService.findUserByNickname(nickname);
        return UserResponse.fromEntity(user);
    }

    // GET - FIND_BY_EMAIL - http://localhost:3001/api/users/email

    @GetMapping("/email")
    public UserResponse getUserByEmail(@RequestParam String email) {
        User user = userService.findUserByEmail(email);
        return UserResponse.fromEntity(user);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/users/{userId}

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponse findUserByIdAndUpdate(
            @PathVariable Long userId,
            @RequestBody @Validated UserUpdateProfileRequest payload,
            BindingResult validationResult
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        User updatedUser = userService.findUserByIdAndUpdate(userId, payload);
        return UserResponse.fromEntity(updatedUser);
    }

    // PUT - FIND_BY_ID_AND_UPDATE_PASSWORD - http://localhost:3001/api/users/{userId}/password

    @PutMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changePassword(
            @PathVariable Long userId,
            @RequestBody @Validated UserChangePasswordRequest payload,
            BindingResult validationResult
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        userService.changePassword(userId, payload);
    }

    // DELETE - FIND_BY_ID_AND_DELETE - http://localhost:3001/api/users/{userId}

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long userId
    ) {
        userService.findUserByIdAndDelete(userId);
    }

    // FIND - FIND_BY_ID_ALL_PROJECTS - http://localhost:3001/api/{userId}/projects

    @GetMapping("/{userId}/projects")
    public List<ProjectResponse> getProjectsOfUser(@PathVariable Long userId) {
        List<Project> projects = projectService.findProjectsByUserId(userId);
        return projects.stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }
}