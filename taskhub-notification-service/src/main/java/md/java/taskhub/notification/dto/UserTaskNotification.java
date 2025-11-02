package md.java.taskhub.notification.dto;

import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.common.enums.TaskEventType;

import java.time.Instant;

@Getter
@Setter
public class UserTaskNotification {
    private String taskId;
    private String assignedToName;
    private String title;
    private String message;
    private TaskEventType eventType;;
    private Instant timestamp;
    private boolean read = false;
}
