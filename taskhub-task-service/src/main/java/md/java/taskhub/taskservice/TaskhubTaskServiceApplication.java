package md.java.taskhub.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "md.java.taskhub.taskservice.client")
public class TaskhubTaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskhubTaskServiceApplication.class, args);
    }

}
