package md.java.taskhub.taskservice.kafka;

import md.java.taskhub.taskservice.event.TaskEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.task-events}")
    private String topicName;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskEvent(TaskEvent taskEvent) {
        String key = taskEvent.getPayload().getTaskId().toString();
        kafkaTemplate.send(topicName, key, taskEvent);
    }
}
