package md.java.taskhub.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import md.java.taskhub.common.events.TaskEvent;
import md.java.taskhub.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventListener {

    private final NotificationService notificationService;

    public TaskEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.task-events}",
            groupId = "${app.kafka.group.notification}",
            containerFactory = "taskEventKafkaListenerFactory"
    )
    public void onMessage(TaskEvent event) {
//        String json = "{\"eventId\":\"9c4145b8-2409-433f-97f1-c9a9c7e3479f\",\"eventType\":\"TASK_CREATED\",\"eventTime\":\"2025-10-30T16:07:11.906851Z\",\"version\":\"1.0\",\"payload\":{\"taskId\":\"bd1e3e8d-3b99-4113-b6cf-fc39239d175a\",\"title\":\"Test Event Producer 7\",\"status\":\"TODO\",\"dueDate\":\"2025-10-30\",\"createdById\":\"f1b6b513-b2b7-4492-96dc-fac8454f2e73\",\"createdByName\":\"mohit\",\"assignedToId\":\"f1b6b513-b2b7-4492-96dc-fac8454f2e73\",\"assignedToName\":\"mohit\"}}";
//        TaskEvent taskEvent = null;
//        try {
//            taskEvent = objectMapper.readValue(json, TaskEvent.class);
//        } catch (JsonProcessingException ex) {
//            System.out.println(ex.getMessage());
//        }
//        System.out.println(taskEvent);
        notificationService.handleEvent(event);
    }
}
