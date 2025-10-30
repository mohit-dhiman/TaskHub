package md.java.taskhub.common.events;

import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.common.dto.TaskEventPayload;
import md.java.taskhub.common.enums.TaskEventType;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class TaskEvent {
    private UUID eventId;
    private TaskEventType eventType;
    private Instant eventTime;

    private TaskEventPayload payload;
}
