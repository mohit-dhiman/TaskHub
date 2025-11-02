package md.java.taskhub.notification.kafka;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import md.java.taskhub.common.events.TaskEvent;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsumerTypeResolver {

    private static String taskEventTopic;

    private static JavaType taskEventType = TypeFactory.defaultInstance().constructType(TaskEvent.class);
    private static JavaType defaultType = TypeFactory.defaultInstance().constructType(Object.class);

    @Value("${app.kafka.topic.task-events.name}")
    public void setTaskEventTopicName(String name) {
        taskEventTopic = name;
    }

    public static JavaType topicBaseJavaType(String topic, byte[] data, Headers headers) {
        if (topic.equals(taskEventTopic)) {
            return taskEventType;
        }
        return defaultType;
    }
}
