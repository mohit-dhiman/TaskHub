package md.java.taskhub.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import md.java.taskhub.common.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtil.objectMapperWithTimeModuleSupport();
    }
}
