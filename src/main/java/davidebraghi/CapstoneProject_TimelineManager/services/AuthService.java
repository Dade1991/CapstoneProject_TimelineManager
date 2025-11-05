package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.User_DTO_RequestsAndResponse.UserLoginRequest;
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

    public String checkAndCreateToken(UserLoginRequest loginPayloadRequest) {

        // trova lo user per uno specifica email

        User userFound = this.userService.findUserByEmail(loginPayloadRequest.email());

        // verifica la password

        if (bcrypt.matches(loginPayloadRequest.password(), userFound.getPassword())) {
            return jwtTools.generateTokenFromUser(userFound);
        } else {
            throw new UnauthorizedException("Credentials not valid. Try again.");
        }
    }
}