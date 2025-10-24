package md.java.taskhub.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class handles unauthorized access.
 * When someone hits a protected API without a valid token, Spring Security will invoke this.
 *
 * 	• Sends a 401 Unauthorized response.
 * 	• Prevents Spring’s default “redirect to login” behavior (which makes no sense for APIs).
 * 	• Keeps responses consistent for clients (Postman, frontend apps).
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
