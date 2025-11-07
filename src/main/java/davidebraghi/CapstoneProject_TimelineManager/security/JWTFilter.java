package davidebraghi.CapstoneProject_TimelineManager.security;

import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

        System.out.println("[JWTFilter] Intercepted: " + request.getServletPath());

        try {
            String authHeader = request.getHeader("Authorization");

            System.out.println("[JWTFilter] Authorization header: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {

                System.out.println("[JWTFilter] No Bearer token found, continuing without authorization");

                filterChain.doFilter(request, response);

                return;
            }

            // estrazione e verifica del token

            String accessToken = authHeader.replace("Bearer ", "");

            System.out.println("[JWTFilter] Token extracted (first 50 chars): " + accessToken.substring(0, Math.min(50, accessToken.length())));

            jwtTools.verifyToken(accessToken);

            System.out.println("[JWTFilter] Token verified successfully");

            // estrazione userId dal token

            Long userId = jwtTools.exctractIdFromToken(accessToken);

            System.out.println("[JWTFilter] User ID extracted: " + userId);

            // ricerca dello user nel DB

            User foundUser = this.userService.findUserById(userId);

            System.out.println("[JWTFilter] User email found: " + foundUser.getEmail());

            // Creazione dell'authentication token

            Authentication authentication = new UsernamePasswordAuthenticationToken(foundUser, null, foundUser.getAuthorities());

            // impostazione dell'authentication nel security context

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("[JWTFilter] Authentication set");

        } catch (RuntimeException ex) {
            System.err.println("[JWTFilter] Error: " + ex.getMessage());
            ex.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    // eclusione di alcuni filtri JWT per determinati end-points

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // salvo in una variabile

//        String path = request.getServletPath();
//        return new AntPathMatcher().match("/api/auth/**", path);

        String path = request.getServletPath();
        boolean shouldSkip = new AntPathMatcher().match("/api/auth/**", path);
        System.out.println("[JWTFilter] Path: " + path + " -> Skip: " + shouldSkip);
        return shouldSkip;
    }
}


// CHECK DI TUTTI SYSTEM.OUT.PRINTLN -------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>