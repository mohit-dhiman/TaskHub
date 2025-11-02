package md.java.taskhub.taskservice.service;

import jakarta.persistence.EntityNotFoundException;
import md.java.taskhub.common.dto.TaskEventPayload;
import md.java.taskhub.common.enums.TaskEventType;
import md.java.taskhub.common.events.TaskEvent;
import md.java.taskhub.taskservice.client.UserClient;
import md.java.taskhub.taskservice.dto.TaskRequestDto;
import md.java.taskhub.taskservice.dto.TaskResponseDto;
import md.java.taskhub.taskservice.dto.UserDto;
import md.java.taskhub.taskservice.entity.Task;
import md.java.taskhub.taskservice.kafka.TaskEventProducer;
import md.java.taskhub.taskservice.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        UserDto owner = getCurrentUser();
        UserDto assignee = getUser(request.getAssignedTo());
        // Create the Task
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(request.getAssignedTo());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setCreatedBy(owner.getId());
        task.setStatus(request.getStatus());
        
        Task saved = taskRepository.save(task);
        publishEvent(TaskEventType.TASK_CREATED, toTaskPayload(saved, owner, assignee));
        if (assignee != null) {
            publishEvent(TaskEventType.TASK_ASSIGNED, toTaskPayload(saved, owner, assignee));
        }
        return toResponseDto(saved, owner, assignee);
    }

    public TaskResponseDto readTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        return toResponseDto(task, getUser(task.getCreatedBy()), getUser(task.getAssignedTo()));
    }

    public TaskResponseDto updateTask(UUID taskId, TaskRequestDto request) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        UserDto currentUser = getCurrentUser();
        // Only Owner or assignee can update the task
        if (!task.getCreatedBy().equals(currentUser.getId()) &&
                (task.getAssignedTo() == null || !task.getAssignedTo().equals(currentUser.getId()))) {
            throw new AccessDeniedException("You are not authorized to update this task");
        }
        UserDto owner = null;
        UserDto currentAssignee = null;
        if (currentUser.getId().equals(task.getCreatedBy())) {
            // Owner is updating the task
            owner = currentUser;
            currentAssignee = getUser(task.getAssignedTo());
        } else {
            // Assignee is updating the task
            currentAssignee = currentUser;
            owner = getUser(task.getCreatedBy());
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(request.getAssignedTo());
        task.setStatus(request.getStatus());
        task.setUpdatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);
        UserDto updatedAssignee = null;
        if (saved.getAssignedTo() != null) {
            if (currentAssignee != null && currentAssignee.getId().equals(saved.getAssignedTo())) {
                updatedAssignee = currentAssignee;
            } else {
                updatedAssignee = getUser(saved.getAssignedTo());
            }
        }
        publishEvent(TaskEventType.TASK_UPDATED, toTaskPayload(saved, owner, updatedAssignee));
        if (updatedAssignee != null && updatedAssignee != currentAssignee) {
            publishEvent(TaskEventType.TASK_ASSIGNED, toTaskPayload(saved, owner, updatedAssignee));
        }
        return toResponseDto(saved, owner, updatedAssignee);
    }

    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task with id: " + taskId + " not found")
        );
        UserDto currentUser = getCurrentUser();
        // Only owner can delete the task
        if (!task.getCreatedBy().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the creator can delete this task");
        }
        UserDto assignee = getUser(task.getAssignedTo());
        taskRepository.delete(task);
        publishEvent(TaskEventType.TASK_DELETED, toTaskPayload(task, currentUser, assignee));
    }

    public List<TaskResponseDto> getMyCreatedTasks() {
        List<TaskResponseDto> tasks = new ArrayList<>();
        UserDto owner = getCurrentUser();
        for(Task task: taskRepository.findByCreatedBy(owner.getId())) {
            UserDto assignee = getUser(task.getAssignedTo());
            tasks.add(toResponseDto(task, owner, assignee));
        }
        return tasks;
    }

    public List<TaskResponseDto> getMyAssignedTasks() {
        List<TaskResponseDto> tasks = new ArrayList<>();
        UserDto assignee = getCurrentUser();
        for (Task task : taskRepository.findByAssignedTo(assignee.getId())) {
            UserDto owner = getUser(task.getCreatedBy());
            tasks.add(toResponseDto(task, owner, assignee));
        }
        return tasks;
    }

    private void publishEvent(TaskEventType type, TaskEventPayload payload) {
        // Create TaskEvent
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setEventId(UUID.randomUUID());
        taskEvent.setEventType(type);
        taskEvent.setEventTime(Instant.now());
        taskEvent.setPayload(payload);
        // Publish to Kafka
        taskEventProducer.sendTaskEvent(taskEvent);
        // Publishing after save() is usually fine,
        // but if you need strict DB-transactional atomicity (produce only if DB commit succeeds),
        // use KafkaTransactionManager or Spring Kafka’s outbox pattern
    }

    private UserDto getUser(UUID id) {
        if (id == null) {
            return null;
        }
        return userClient.getUserById(id);
    }

    private TaskResponseDto toResponseDto(Task task, UserDto owner, UserDto assignee) {
        TaskResponseDto response = new TaskResponseDto();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setStatus(task.getStatus());
        response.setCreatedBy(task.getCreatedBy());
        response.setCreatedByName(owner.getUsername());
        if (assignee != null) {
            response.setAssignedTo(assignee.getId());
            response.setAssignedToName(assignee.getUsername());
        }
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }

    private TaskEventPayload toTaskPayload(Task task, UserDto owner, UserDto assignee) {
        TaskEventPayload payload = new TaskEventPayload();
        payload.setTaskId(task.getId());
        payload.setTitle(task.getTitle());
        payload.setStatus(task.getStatus());
        payload.setDueDate(task.getDueDate());
        payload.setCreatedById(task.getCreatedBy());
        payload.setCreatedByName(owner.getUsername());
        if (assignee != null) {
            payload.setAssignedToId(assignee.getId());
            payload.setAssignedToName(assignee.getUsername());
        }
        return  payload;
    }

    private UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto principalUser = (UserDto) authentication.getPrincipal();
        return principalUser;
    }

}
