package md.java.taskhub.common.events;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import md.java.taskhub.common.dto.TaskEventPayload;
import md.java.taskhub.common.enums.TaskEventType;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
public class TaskEvent {
    private UUID eventId;
    private TaskEventType eventType;
    private Instant eventTime;
    private String version = "1.0";

    private TaskEventPayload payload;
}
