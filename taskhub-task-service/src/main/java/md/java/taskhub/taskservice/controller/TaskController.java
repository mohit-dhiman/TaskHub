package md.java.taskhub.taskservice.controller;

import md.java.taskhub.taskservice.dto.TaskRequestDto;
import md.java.taskhub.taskservice.dto.TaskResponseDto;
import md.java.taskhub.taskservice.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Validated @RequestBody TaskRequestDto request) {
        TaskResponseDto response = taskService.createTask(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created")
    public ResponseEntity<List<TaskResponseDto>> getMyCreatedTasks() {
        List<TaskResponseDto> tasks = taskService.getMyCreatedTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<TaskResponseDto>> getMyAssignedTasks() {
        List<TaskResponseDto> tasks = taskService.getMyAssignedTasks();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable UUID id,
                                                      @Validated @RequestBody TaskRequestDto request) {
        TaskResponseDto task = taskService.updateTask(id, request);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
