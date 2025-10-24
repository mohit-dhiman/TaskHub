package md.java.taskhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
public class TaskHubApplication {

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        Date date = new Date();
        SpringApplication.run(TaskHubApplication.class, args);
    }

}
