package md.java.taskhub.taskservice.service;

import jakarta.persistence.EntityNotFoundException;
import md.java.taskhub.taskservice.client.UserClient;
import md.java.taskhub.taskservice.dto.TaskRequestDto;
import md.java.taskhub.taskservice.dto.TaskResponseDto;
import md.java.taskhub.taskservice.dto.UserDto;
import md.java.taskhub.taskservice.entity.Task;
import md.java.taskhub.taskservice.event.TaskEvent;
import md.java.taskhub.taskservice.event.TaskEventType;
import md.java.taskhub.taskservice.event.TaskPayload;
import md.java.taskhub.taskservice.kafka.TaskEventProducer;
import md.java.taskhub.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventProducer taskEventProducer;
    private final UserClient userClient;

    public TaskService(TaskRepository taskRepository, TaskEventProducer taskEventProducer, UserClient userClient) {
        this.taskRepository = taskRepository;
        this.taskEventProducer = taskEventProducer;
        this.userClient = userClient;
    }

    public TaskResponseDto createTask(TaskRequestDto request) {
        //TODO: getCurrentUSer
        UserDto user = userClient.getUserById(UUID.randomUUID());
        //User user = authService.getCurrentUser();
        // Create the Task
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(request.getAssignedTo());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setCreatedBy(user.getId());
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
        //TODO: getCurrentUSer
        UserDto user = userClient.getUserById(UUID.randomUUID());
        return taskRepository.findByCreatedBy(user.getId())
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDto> getMyAssignedTasks() {
        //TODO: getCurrentUSer
        UserDto user = userClient.getUserById(UUID.randomUUID());
        return taskRepository.findByAssignedTo(user.getId())
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto updateTask(UUID taskId, TaskRequestDto request) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        //TODO: getCurrentUSer
        UserDto currentUser = userClient.getUserById(UUID.randomUUID());
        // Only Owner or assignee can update the task
        if (!task.getCreatedBy().equals(currentUser.getId()) &&
                (task.getAssignedTo() == null || !task.getAssignedTo().equals(currentUser.getId()))) {
            // TODO
            //throw new AccessDeniedException("You are not authorized to update this task");
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(request.getAssignedTo());
        task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        return toResponseDto(taskRepository.save(task));
    }

    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        //TODO: getCurrentUSer
        UserDto currentUser = userClient.getUserById(UUID.randomUUID());
        // Only owner can delete the task
        if (!task.getCreatedBy().equals(currentUser.getId())) {
            // TODO
            //throw new AccessDeniedException("Only the creator can delete this task");
        }
        taskRepository.delete(task);
    }

    private UserDto getUser(UUID id) {
        return userClient.getUserById(id);
    }

    private TaskResponseDto toResponseDto(Task task) {
        TaskResponseDto response = new TaskResponseDto();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setStatus(task.getStatus());
        response.setCreatedBy(task.getCreatedBy());
        //TODO
        //response.setCreatedByName(task.getCreatedBy().getUsername());
        if (task.getAssignedTo() != null) {
            response.setAssignedTo(task.getAssignedTo());
            // TODO
            //response.setAssignedToName(task.getAssignedTo().getUsername());
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
        payload.setCreatedById(task.getCreatedBy());
        // TODO
        // payload.setCreatedByName(task.getCreatedBy().getUsername());
        if (task.getAssignedTo() != null) {
            payload.setAssignedToId(task.getAssignedTo());
            // TODO
            // payload.setAssignedToName(task.getAssignedTo().getUsername());
        }
        return  payload;
    }
}
