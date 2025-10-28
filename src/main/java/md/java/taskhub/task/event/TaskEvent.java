package md.java.taskhub.task.event;

import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.task.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskEvent {
    private UUID eventId;
    private TaskEventType eventType;
    private Instant eventTime;

    private TaskPayload payload;
}
