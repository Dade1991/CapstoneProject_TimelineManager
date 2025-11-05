package davidebraghi.CapstoneProject_TimelineManager.security;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // verifica della header authorization

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnavailableException("Insert the token in the 'authorization' header - Used format: Bearer <token>");
            }

            // estrazione e verifica del token

            String accessToken = authHeader.replace("Bearer ", "");
            jwtTools.verifyToken(accessToken);

            // estrazione userId dal token

            Long userId = jwtTools.exctractIdFromToken(accessToken);

            // ricerca dello user nel DB

            User foundUser = this.userService.findUserById(userId);

            // Creazione dell'authentication token

            Authentication authentication = new UsernamePasswordAuthenticationToken(foundUser, null, foundUser.getAuthorities());

            // impostazione dell'authentication nel security context

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    // eclusione di alcuni filtri JWT per determinati end-points

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // salvo in una variabile

        String path = request.getServletPath();
        return new AntPathMatcher().match("/auth/**", path);
    }
}
