package md.java.taskhub.task.dto;

import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.task.entity.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TaskResponseDto {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private UUID assignedTo;
    private String assignedToName;
    private UUID createdBy;
    private String createdByName;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
