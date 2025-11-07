package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserChangePasswordRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserUpdateProfileRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET - FIND_BY_ID (profilo user loggato) - http://localhost:3001/api/users/me

    @GetMapping("/me")
    public User getPersonalProfile(
            @AuthenticationPrincipal User userPersonalProfile
    ) {
        return userPersonalProfile;
    }

    // GET - FIND_BY_ID - http://localhost:3001/api/users/id/{userId}

    @GetMapping("/id/{userId}")
    public User getUserById(
            @PathVariable Long userId
    ) {
        return userService.findUserById(userId);
    }

    // GET - FIND_BY_NICKNAME - http://localhost:3001/api/users/nickname/{nickname}

    @GetMapping("/nickname/{nickname}")
    public User getUserByNickname(
            @PathVariable String nickname
    ) {
        return userService.findUserByNickname(nickname);
    }

    // GET - FIND_BY_EMAIL - http://localhost:3001/api/users/email

    @GetMapping("/email")
    public User getUserByEmail(
            @RequestParam String email
    ) {
        return userService.findUserByEmail(email);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/users/{userId}

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User findUserByIdAndUpdate(
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
        return userService.findUserByIdAndUpdate(userId, payload);
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
}