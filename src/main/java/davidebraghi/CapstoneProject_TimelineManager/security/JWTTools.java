package davidebraghi.CapstoneProject_TimelineManager.security;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JWTTools {
    @Value("${jwt.secret}")
    private String keySecret;

    // creazione token

    public String generateTokenFromUser(User user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder().
                issuedAt(new Date(System.currentTimeMillis())).
                expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)).
                subject(String.valueOf(user.getUserId())).
                claim("roles", roles).
                signWith(Keys.hmacShaKeyFor(keySecret.getBytes())).
                compact();
    }

    // validità token

    public void verifyToken(String accessToken) {
        Jwts.parser().
                verifyWith(Keys.hmacShaKeyFor(keySecret.getBytes())).
                build().
                parse(accessToken);
    }

    // estrazione dell'ID dal token

    public Long extractIdFromToken(String accessToken) {
        return Long.parseLong(Jwts.parser().
                verifyWith(Keys.hmacShaKeyFor(keySecret.getBytes())).
                build().
                parseSignedClaims(accessToken).
                getPayload().
                getSubject());
    }
}