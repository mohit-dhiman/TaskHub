package md.java.taskhub.taskservice.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class TaskEvent {
    private UUID eventId;
    private TaskEventType eventType;
    private Instant eventTime;

    private TaskPayload payload;
}
