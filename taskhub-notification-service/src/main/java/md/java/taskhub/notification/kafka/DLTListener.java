package md.java.taskhub.notification.kafka;

import md.java.taskhub.common.events.TaskEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * DLT shouldn't be consumed like this
 */
@Component
public class DLTListener {

    @KafkaListener(
            topics = "${app.kafka.topic.task-events.name}.DLT",
            groupId = "${app.kafka.group.notification}-dlt",
            containerFactory = "taskEventKafkaListenerFactory")
    public void handleTaskEventDLT(ConsumerRecord<String, TaskEvent> record) {
        Headers headers = record.headers();
        Header exMessage = headers.lastHeader("kafka_dlt-exception-message");
        String cause = exMessage != null ? new String(exMessage.value(), StandardCharsets.UTF_8) : "unknown";
        System.out.println("DLT message for original topic=" + headers.lastHeader("kafka_dlt-original-topic") +
                ", partition=" + headers.lastHeader("kafka_dlt-original-partition") +
                ", offset="+headers.lastHeader("kafka_dlt-original-offset") + ", cause=" + cause);
    }
}
