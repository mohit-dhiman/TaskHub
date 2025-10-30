package md.java.taskhub.taskservice.client;

import md.java.taskhub.taskservice.config.FeignClientConfig;
import md.java.taskhub.taskservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "${app.auth.service.name}", url = "${app.auth.service.url}",
        configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/api/auth/{id}")
    UserDto getUserById(@PathVariable UUID id);

    @GetMapping("/api/auth/me")
    UserDto getCurrentUser();
}
