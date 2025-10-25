package md.java.taskhub.task.service;

import jakarta.persistence.EntityNotFoundException;
import md.java.taskhub.auth.entity.User;
import md.java.taskhub.auth.repository.UserRepository;
import md.java.taskhub.auth.service.AuthService;
import md.java.taskhub.task.dto.TaskRequestDto;
import md.java.taskhub.task.dto.TaskResponseDto;
import md.java.taskhub.task.entity.Task;
import md.java.taskhub.task.repository.TaskRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    TaskRepository taskRepository;
    UserRepository userRepository;
    AuthService authService;

    public TaskService(TaskRepository taskRepository,  UserRepository userRepository,  AuthService authService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public TaskResponseDto createTask(TaskRequestDto request) {
        User user = authService.getCurrentUser();
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
        Task saved = taskRepository.save(task);
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
}
