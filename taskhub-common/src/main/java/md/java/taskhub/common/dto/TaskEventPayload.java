package md.java.taskhub.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import md.java.taskhub.common.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class TaskEventPayload {
    private UUID taskId;
    private String title;
    private TaskStatus status;
    private LocalDate dueDate;
    private UUID createdById;
    private String createdByName;
    private UUID assignedToId;
    private String assignedToName;
}
