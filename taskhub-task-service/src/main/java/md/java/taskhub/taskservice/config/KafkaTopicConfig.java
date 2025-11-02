package md.java.taskhub.taskservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.task-events.name}")
    private String taskEventTopicName;
    @Value("${app.kafka.topic.task-events.partitions}")
    private Integer partitions;
    @Value("${app.kafka.topic.task-events.replicas}")
    private Integer replicas;

    @Bean
    public NewTopic taskEventTopic() {
        return TopicBuilder.name(taskEventTopicName).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic taskEventDLTTopic() {
        return TopicBuilder.name(taskEventTopicName + ".DLT").partitions(partitions).replicas(replicas).build();
    }
}
