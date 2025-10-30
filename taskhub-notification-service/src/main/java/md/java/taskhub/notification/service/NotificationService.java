package md.java.taskhub.notification.service;

import md.java.taskhub.common.events.TaskEvent;
import org.springframework.stereotype.Service;

/**
 * This service needs to be idempotent
 */
@Service
public class NotificationService {

    public void handleEvent(TaskEvent taskEvent) {
        System.out.println("Received task event: " + taskEvent.getEventId());
    }
}
