package md.java.taskhub.taskservice.kafka;

import md.java.taskhub.common.events.TaskEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.task-events.name}")
    private String topicName;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskEvent(TaskEvent taskEvent) {
        String key = taskEvent.getPayload().getTaskId().toString();
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, key, taskEvent);
        future.whenComplete((result, ex) -> {
           if (ex != null) {
               System.out.println("Unable to send task event: " + ex.getMessage());
           } else {
               System.out.println("Successfully sent task event: " + result);
           }
        });
    }
}
