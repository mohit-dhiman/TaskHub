package md.java.taskhub.taskservice.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This will intercept and copy the Authorization header from the incoming request to the feign client Request Template
 */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if (request != null) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String authHeader = httpServletRequest.getHeader("Authorization");

            if (authHeader != null && !authHeader.isEmpty()) {
                requestTemplate.header("Authorization", authHeader);
            }
        }
    }
}
