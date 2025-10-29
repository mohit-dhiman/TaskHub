package md.java.taskhub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import md.java.taskhub.common.exception.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

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

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

    }


    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse();
        apiErrorResponse.setTimestamp(LocalDateTime.now());
        apiErrorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiErrorResponse.setError("Unauthorized");
        apiErrorResponse.setMessage(authException.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
        response.getWriter().flush();
    }
}
