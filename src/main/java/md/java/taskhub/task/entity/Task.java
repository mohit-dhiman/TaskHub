package md.java.taskhub.task.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.auth.entity.User;
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

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

