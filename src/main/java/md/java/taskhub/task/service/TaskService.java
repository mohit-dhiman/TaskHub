package md.java.taskhub.task.service;

import jakarta.persistence.EntityNotFoundException;
import md.java.taskhub.auth.entity.User;
import md.java.taskhub.auth.repository.UserRepository;
import md.java.taskhub.auth.service.AuthService;
import md.java.taskhub.task.dto.TaskRequestDto;
import md.java.taskhub.task.dto.TaskResponseDto;
import md.java.taskhub.task.entity.Task;
import md.java.taskhub.task.event.TaskEvent;
import md.java.taskhub.task.event.TaskEventType;
import md.java.taskhub.task.event.TaskPayload;
import md.java.taskhub.task.kafka.TaskEventProducer;
import md.java.taskhub.task.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final TaskEventProducer taskEventProducer;

    public TaskService(TaskRepository taskRepository,  UserRepository userRepository,
                       AuthService authService, TaskEventProducer taskEventProducer) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.taskEventProducer = taskEventProducer;
    }

    public TaskResponseDto createTask(TaskRequestDto request) {
        User user = authService.getCurrentUser();
        // Create the Task
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        if (request.getAssignedTo() != null) {
            task.setAssignedTo(getUser(request.getAssignedTo()));
        }
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setCreatedBy(user);
        task.setStatus(request.getStatus());
        // Save to Database
        Task saved = taskRepository.save(task);

        // Create TaskEvent
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setEventId(UUID.randomUUID());
        taskEvent.setEventType(TaskEventType.TASK_CREATED);
        taskEvent.setEventTime(Instant.now());
        taskEvent.setPayload(toTaskPayload(saved));
        // Publish to Kafka
        taskEventProducer.sendTaskEvent(taskEvent);
        // Publishing after save() is usually fine,
        // but if you need strict DB-transactional atomicity (produce only if DB commit succeeds),
        // use KafkaTransactionManager or Spring Kafka’s outbox pattern

        return toResponseDto(saved);
    }

    public List<TaskResponseDto> getMyCreatedTasks() {
        User user = authService.getCurrentUser();
        return taskRepository.findByCreatedBy(user)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDto> getMyAssignedTasks() {
        User user = authService.getCurrentUser();
        return taskRepository.findByAssignedTo(user)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto updateTask(UUID taskId, TaskRequestDto request) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        User currentUser = authService.getCurrentUser();
        // Only Owner or assignee can update the task
        if (!task.getCreatedBy().getId().equals(currentUser.getId()) &&
                (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(currentUser.getId()))) {
            throw new AccessDeniedException("You are not authorized to update this task");
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        if (request.getAssignedTo() != null) {
            task.setAssignedTo(getUser(request.getAssignedTo()));
        }
        task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        return toResponseDto(taskRepository.save(task));
    }

    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        User currentUser = authService.getCurrentUser();
        // Only owner can delete the task
        if (!task.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the creator can delete this task");
        }
        taskRepository.delete(task);
    }

    private User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found with Id: " + id)
        );
    }

    private TaskResponseDto toResponseDto(Task task) {
        TaskResponseDto response = new TaskResponseDto();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setStatus(task.getStatus());
        response.setCreatedBy(task.getCreatedBy().getId());
        response.setCreatedByName(task.getCreatedBy().getUsername());
        if (task.getAssignedTo() != null) {
            response.setAssignedTo(task.getAssignedTo().getId());
            response.setAssignedToName(task.getAssignedTo().getUsername());
        }
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    private TaskPayload toTaskPayload(Task task) {
        TaskPayload payload = new TaskPayload();
        payload.setTaskId(task.getId());
        payload.setTitle(task.getTitle());
        payload.setStatus(task.getStatus());
        payload.setDueDate(task.getDueDate());
        payload.setCreatedById(task.getCreatedBy().getId());
        payload.setCreatedByName(task.getCreatedBy().getUsername());
        if (task.getAssignedTo() != null) {
            payload.setAssignedToId(task.getAssignedTo().getId());
            payload.setAssignedToName(task.getAssignedTo().getUsername());
        }
        return  payload;
    }
}
