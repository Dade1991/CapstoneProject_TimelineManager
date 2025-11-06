package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.AuthResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Signup_DTO_RequestsAndResponses.SignupRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserLoginRequest;
import davidebraghi.CapstoneProject_TimelineManager.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST - login - http://localhost:3001/api/auth/login

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody UserLoginRequest payload
    ) {
        return authService.login(payload);
    }

    // POST - signup - http://localhost:3001/api/auth/signup

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signup(
            @RequestBody SignupRequest payload
    ) {
        return authService.signup(payload);
    }
}