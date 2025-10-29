package md.java.taskhub.taskservice.event;

import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.taskservice.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskPayload {
    private UUID taskId;
    private String title;
    private TaskStatus status;
    private LocalDate dueDate;
    private UUID createdById;
    private String createdByName;
    private UUID assignedToId;
    private String assignedToName;
}
