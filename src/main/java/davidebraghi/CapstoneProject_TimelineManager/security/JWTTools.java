package davidebraghi.CapstoneProject_TimelineManager.security;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTTools {
    @Value("${jwt.secret}")
    private String keySecret;

    // Creazione token

    public String generateTokenFromUser(User user) {
        return Jwts.builder().
                issuedAt(new Date(System.currentTimeMillis())).
                expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)).
                subject(String.valueOf(user.getUserId())).
                signWith(Keys.hmacShaKeyFor(keySecret.getBytes())).
                compact();
    }

    // Validità token

    public void verifyToken(String accessToken) {
        Jwts.parser().
                verifyWith(Keys.hmacShaKeyFor(keySecret.getBytes())).
                build().
                parse(accessToken);
    }

    // Estrazione dell'ID dal token

    public Long exctractIdFromToken(String accessToken) {
        return Long.parseLong(Jwts.parser().
                verifyWith(Keys.hmacShaKeyFor(keySecret.getBytes())).
                build().
                parseSignedClaims(accessToken).
                getPayload().
                getSubject());
    }
}