package md.java.taskhub.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.taskservice.entity.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private TaskStatus status = TaskStatus.TODO;
    private UUID assignedTo;
    private LocalDate dueDate;
}
