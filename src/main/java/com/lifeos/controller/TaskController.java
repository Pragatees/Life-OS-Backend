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

import com.lifeos.dto.response.TaskHistoryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing a user's tasks.
 *
 * Base path: /api/tasks
 *
 * Endpoints:
 *   POST   /api/tasks                -> create a new task
 *   GET    /api/tasks/today          -> list today's tasks
 *   PATCH  /api/tasks/{taskId}/complete -> mark a task as completed
 *   PUT    /api/tasks/{taskId}       -> update an existing task
 *   DELETE /api/tasks/{taskId}       -> delete a task
 *
 * All endpoints require authentication; the authenticated user is
 * resolved via {@link UserPrincipal} and every task operation is
 * scoped to that user (users cannot access each other's tasks).
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // -------------------------------------------------------------------
    // POST /api/tasks
    // Create a new task for the authenticated user.
    // -------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        Task task = new Task();
        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setTaskDate(request.getTaskDate());
        task.setTaskTime(request.getTaskTime());
        task.setPriority(request.getPriority());
        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedTask));
    }

    // -------------------------------------------------------------------
    // GET /api/tasks/today
    // Return all tasks scheduled for today, ordered by time (ascending).
    // -------------------------------------------------------------------
    @GetMapping("/today")
    public ResponseEntity<List<TaskResponse>> getTodayTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);
        LocalDate today = LocalDate.now();

        List<Task> tasks = taskRepository.findByUserAndTaskDateOrderByTaskTimeAsc(user, today);

        List<TaskResponse> response = tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------
    // PATCH /api/tasks/{taskId}/complete
    // Mark a single task as completed.
    // -------------------------------------------------------------------
    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);
        Task task = getTaskOrThrow(taskId, user);

        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(toResponse(updatedTask));
    }

    // -------------------------------------------------------------------
    // PUT /api/tasks/{taskId}
    // Replace an existing task's fields with the values in the request.
    // -------------------------------------------------------------------
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);
        Task task = getTaskOrThrow(taskId, user);

        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setTaskDate(request.getTaskDate());
        task.setTaskTime(request.getTaskTime());
        task.setPriority(request.getPriority());

        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(toResponse(updatedTask));
    }

    // -------------------------------------------------------------------
    // DELETE /api/tasks/{taskId}
    // Permanently delete a task.
    // -------------------------------------------------------------------
    @DeleteMapping("/{taskId}")
    public ResponseEntity<MessageResponse> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);
        Task task = getTaskOrThrow(taskId, user);

        taskRepository.delete(task);

        return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
    }

    // -------------------------------------------------------------------
    // GET /api/tasks/range
    // Return all tasks between the given start and end dates.
    // -------------------------------------------------------------------
    @GetMapping("/range")
    public ResponseEntity<List<TaskHistoryResponse>> getTasksByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        List<Task> tasks = taskRepository
                .findByUserAndTaskDateBetweenOrderByTaskDateAscTaskTimeAsc(
                        user,
                        start,
                        end
                );

        List<TaskHistoryResponse> response = tasks.stream()
                .map(this::toTaskHistoryResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // =====================================================================
    // Helper methods
    // =====================================================================

    /**
     * Resolves the authenticated user, or throws if the user no longer exists.
     */
    private User getUserOrThrow(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Resolves a task by id, scoped to the given user, or throws if not found.
     * Scoping by user prevents one user from accessing another user's tasks.
     */
    private Task getTaskOrThrow(UUID taskId, User user) {
        return taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    /**
     * Maps a {@link Task} entity to its outward-facing {@link TaskResponse} DTO.
     */
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTaskName(),
                task.getDescription(),
                task.getTaskDate(),
                task.getTaskTime(),
                task.getPriority(),
                task.getCompleted()
        );
    }

    /**
     * Maps a Task entity to TaskHistoryResponse.
     */
    private TaskHistoryResponse toTaskHistoryResponse(Task task) {
        return new TaskHistoryResponse(
                task.getId(),
                task.getTaskName(),
                task.getDescription(),
                task.getTaskDate(),
                task.getTaskTime(),
                task.getPriority(),
                task.getCompleted()
        );
    }
}