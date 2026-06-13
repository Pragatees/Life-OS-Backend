package com.lifeos.controller;

import com.lifeos.dto.request.CreateTaskRequest;
import com.lifeos.dto.request.UpdateTaskRequest;
import com.lifeos.dto.response.MessageResponse;
import com.lifeos.dto.response.TaskResponse;
import com.lifeos.entity.Task;
import com.lifeos.entity.User;
import com.lifeos.exception.TaskNotFoundException;
import com.lifeos.exception.UserNotFoundException;
import com.lifeos.repository.TaskRepository;
import com.lifeos.repository.UserRepository;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository,
                          UserRepository userRepository) {

        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Task task = new Task();

        task.setTaskName(request.getTaskName());
        task.setTaskDate(request.getTaskDate());
        task.setTaskTime(request.getTaskTime());

        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        TaskResponse response = new TaskResponse(
                savedTask.getId(),
                savedTask.getTaskName(),
                savedTask.getTaskDate(),
                savedTask.getTaskTime(),
                savedTask.getCompleted()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/today")
    public ResponseEntity<List<TaskResponse>> getTodayTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        LocalDate today = LocalDate.now();

        List<Task> tasks = taskRepository
                .findByUserAndTaskDateOrderByTaskTimeAsc(
                        user,
                        today
                );

        List<TaskResponse> response = tasks.stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTaskName(),
                        task.getTaskDate(),
                        task.getTaskTime(),
                        task.getCompleted()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Task task = taskRepository
                .findByIdAndUser(taskId, user)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found"));

        task.setCompleted(true);

        Task updatedTask = taskRepository.save(task);

        TaskResponse response = new TaskResponse(
                updatedTask.getId(),
                updatedTask.getTaskName(),
                updatedTask.getTaskDate(),
                updatedTask.getTaskTime(),
                updatedTask.getCompleted()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<MessageResponse> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Task task = taskRepository
                .findByIdAndUser(taskId, user)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found"));

        taskRepository.delete(task);

        return ResponseEntity.ok(
                new MessageResponse("Task deleted successfully")
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Task task = taskRepository
                .findByIdAndUser(taskId, user)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task not found"));

        task.setTaskName(request.getTaskName());
        task.setTaskDate(request.getTaskDate());
        task.setTaskTime(request.getTaskTime());

        Task updatedTask = taskRepository.save(task);

        TaskResponse response = new TaskResponse(
                updatedTask.getId(),
                updatedTask.getTaskName(),
                updatedTask.getTaskDate(),
                updatedTask.getTaskTime(),
                updatedTask.getCompleted()
        );

        return ResponseEntity.ok(response);
    }
}

