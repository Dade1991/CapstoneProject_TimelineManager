package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.AuthResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Signup_DTO_RequestsAndResponses.SignupRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserLoginRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserRegisterRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.UnauthorizedException;
import davidebraghi.CapstoneProject_TimelineManager.security.JWTTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder bcrypt;

    @Autowired
    private JWTTools jwtTools;

    // -------------------- METODO PER LOGIN --------------------

    public AuthResponse login(UserLoginRequest loginPayloadRequest) {

        // trova lo user per uno specifica email

        User userFound = this.userService.findUserByEmail(loginPayloadRequest.email());

        // verifica la password

        if (bcrypt.matches(loginPayloadRequest.password(), userFound.getPassword())) {

            // genera il token

            String accessToken = jwtTools.generateTokenFromUser(userFound);

            // crea una risposta per lo user

            UserResponse userResponse = UserResponse.fromEntity(userFound);

            // ritorna la risposta di autorizzazione

            return new AuthResponse(accessToken, "Bearer ", userResponse);
        } else {
            throw new UnauthorizedException("Credentials not valid. Try again.");
        }
    }

    // -------------------- METODO PER SIGNUP --------------------

    public AuthResponse signup(SignupRequest signupPayloadRequest) {

        // salva lo user e verifica i dati

        User savedUser = userService.savedUser(
                new UserRegisterRequest(
                        signupPayloadRequest.name(),
                        signupPayloadRequest.surname(),
                        signupPayloadRequest.nickname(),
                        signupPayloadRequest.email(),
                        signupPayloadRequest.password()
                )
        );

        // genera il token

        String accessToken = jwtTools.generateTokenFromUser(savedUser);

        UserResponse userResponse = UserResponse.fromEntity(savedUser);

        return new AuthResponse(accessToken, "Bearer ", userResponse);
    }
}