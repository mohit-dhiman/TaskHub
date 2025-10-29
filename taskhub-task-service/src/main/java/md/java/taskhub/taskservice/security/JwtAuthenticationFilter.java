package md.java.taskhub.taskservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import md.java.taskhub.taskservice.client.UserClient;
import md.java.taskhub.taskservice.dto.UserDto;
import md.java.taskhub.taskservice.util.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This class is the gatekeeper.
 * It intercepts every incoming HTTP request before it reaches the controller, and decides:
 * 	•	“Does this request have a valid JWT token?”
 * 	•	“If yes, who’s the user?”
 * 	•	“Should I let this request through as an authenticated user?”
 *
 * If the authentication is missing or invalid, passes the request through without setting authentication
 *
 * In short: this file validates the token and sets the logged-in user context.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserClient userClient;

    public JwtAuthenticationFilter(JwtService jwtService, UserClient userClient) {
        this.jwtService = jwtService;
        this.userClient = userClient;
    }

    // If there's a JWT token in the Authorization header set the user details into the Security Context
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String username;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            username = jwtService.extractUsername(jwtToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDto user = userClient.getCurrentUser();
                if (user != null && jwtService.isTokenValid(jwtToken, username)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
