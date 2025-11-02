package md.java.taskhub.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import md.java.taskhub.common.dto.TaskEventPayload;
import md.java.taskhub.common.enums.TaskEventType;
import md.java.taskhub.common.events.TaskEvent;
import md.java.taskhub.notification.dto.UserTaskNotification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * This service needs to be idempotent
 */
@Service
public class NotificationService {

    private final StringRedisTemplate redisTemplate;
    private final EmailSender emailSender;
    private final ObjectMapper objectMapper;

    public NotificationService(StringRedisTemplate redisTemplate, EmailSender emailSender, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.emailSender = emailSender;
        this.objectMapper = objectMapper;
    }

    public void handleEvent(TaskEvent event) {
        String eventKey = getProcessedEventKey(event.getEventId().toString());
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(eventKey, "1", Duration.ofDays(7));
        if(Boolean.FALSE.equals(wasSet)) {
            // already processed this event
            return;
        }
        if (TaskEventType.TASK_ASSIGNED ==  event.getEventType()) {
            TaskEventPayload payload = event.getPayload();
            if (payload.getAssignedToId() != null) {
                String emailAddress = getEmailAddress(payload.getAssignedToId(), payload.getAssignedToName());
                String subject = "New task assigned: " + payload.getTitle();
                String body =  "You've been assigned a new Task: " + payload.getTitle();
                emailSender.sendEmail(emailAddress, subject, body);

                String notificationKey = getUserNotificationKey(payload.getAssignedToId().toString());
                String jsonNotif = userTaskAssignedNotification(payload);
                redisTemplate.opsForList().leftPush(notificationKey, jsonNotif);
                redisTemplate.opsForList().trim(notificationKey, 0, 49);
            }
        }
    }

    private String getProcessedEventKey(String id) {
        return "processed:event:" + id;
    }

    private String getUserNotificationKey(String id) {
        return "notification:user:" + id;
    }

    // TODO: come on
    private String getEmailAddress(UUID userId, String username) {
        return  username + "@taskhub.com";
    }

    private String userTaskAssignedNotification(TaskEventPayload payload) {
        UserTaskNotification notif = new UserTaskNotification();
        notif.setTaskId(payload.getTaskId().toString());
        notif.setAssignedToName(payload.getAssignedToName());
        notif.setTitle(payload.getTitle());
        notif.setMessage("You have been assigned a new Task: '" + payload.getTitle() + "'");
        // TODO: is this correct?
        notif.setTimestamp(Instant.now());
        try {
            return objectMapper.writeValueAsString(notif);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
