package md.java.taskhub.taskservice.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.common.enums.TaskStatus;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "tasks")
public class Task {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    // TODO: It's currently present in the common module
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private UUID assignedTo;

    @Column(nullable = false)
    private UUID createdBy;

    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

