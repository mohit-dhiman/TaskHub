package md.java.taskhub.taskservice.config;

import feign.RequestInterceptor;
import md.java.taskhub.taskservice.security.FeignAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    private final FeignAuthInterceptor feignAuthInterceptor;

    public FeignClientConfig(FeignAuthInterceptor feignAuthInterceptor) {
        this.feignAuthInterceptor = feignAuthInterceptor;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return feignAuthInterceptor;
    }
}
