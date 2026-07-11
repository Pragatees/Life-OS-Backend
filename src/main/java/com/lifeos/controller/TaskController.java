package com.lifeos.controller;

import com.lifeos.dto.request.CreateTaskRequest;
import com.lifeos.dto.request.UpdateTaskRequest;
import com.lifeos.dto.response.MessageResponse;
import com.lifeos.dto.response.TaskHistoryResponse;
import com.lifeos.dto.response.TaskResponse;
import com.lifeos.entity.RepeatType;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing a user's tasks.
 *
 * <p>Base path: {@code /api/tasks}
 *
 * <p>Endpoints:
 * <ul>
 *   <li>{@code POST   /api/tasks}                 -> create a new task</li>
 *   <li>{@code GET    /api/tasks/today}            -> list today's tasks</li>
 *   <li>{@code GET    /api/tasks/range}            -> list tasks in a date range</li>
 *   <li>{@code PATCH  /api/tasks/{taskId}/complete} -> mark a task as completed</li>
 *   <li>{@code PUT    /api/tasks/{taskId}}         -> update an existing task</li>
 *   <li>{@code DELETE /api/tasks/{taskId}}         -> delete a task</li>
 * </ul>
 *
 * <p>All endpoints require authentication; the authenticated user is resolved via
 * {@link UserPrincipal}, and every task operation is scoped to that user (users
 * cannot access each other's tasks).
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

    // =====================================================================
    // Endpoints
    // =====================================================================

    /**
     * Creates a new task for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User user = getUserOrThrow(userPrincipal);

        Task task = new Task();
        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setTaskDate(request.getTaskDate());
        task.setTaskTime(request.getTaskTime());
        task.setPriority(request.getPriority());
        applyRecurrenceSettings(task, request.getRepeatType());

        task.setUser(user);

        Task savedTask = taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedTask));
    }

    /**
     * Returns all tasks scheduled for today, ordered by time (ascending).
     */
    @GetMapping("/today")
    public ResponseEntity<List<TaskResponse>> getTodayTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User user = getUserOrThrow(userPrincipal);
        LocalDate today = LocalDate.now();

        List<Task> tasks = taskRepository.findByUserAndTaskDateOrderByTaskTimeAsc(user, today);

        List<TaskResponse> response = tasks.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Returns all tasks between the given start and end dates (inclusive),
     * ordered by date and then time.
     */
    @GetMapping("/range")
    public ResponseEntity<List<TaskHistoryResponse>> getTasksByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User user = getUserOrThrow(userPrincipal);

        List<Task> tasks = taskRepository.findByUserAndTaskDateBetweenOrderByTaskDateAscTaskTimeAsc(
                user, start, end);

        List<TaskHistoryResponse> response = tasks.stream()
                .map(this::toTaskHistoryResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Marks a single task as completed.
     */
    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User user = getUserOrThrow(userPrincipal);
        Task task = getTaskOrThrow(taskId, user);

        task.setCompleted(true);
        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(toResponse(updatedTask));
    }

    /**
     * Replaces an existing task's fields with the values in the request.
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User user = getUserOrThrow(userPrincipal);
        Task task = getTaskOrThrow(taskId, user);

        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setTaskDate(request.getTaskDate());
        task.setTaskTime(request.getTaskTime());
        task.setPriority(request.getPriority());
        applyRecurrenceSettings(task, request.getRepeatType());

        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(toResponse(updatedTask));
    }

    /**
     * Permanently deletes a task.
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<MessageResponse> deleteTask(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        User user = getUserOrThrow(userPrincipal);
        Task task = getTaskOrThrow(taskId, user);

        taskRepository.delete(task);

        return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
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
     * Returns the given repeat type, or {@link RepeatType#NEVER} if none was provided.
     */
    private RepeatType resolveRepeatType(RepeatType repeatType) {
        return repeatType != null ? repeatType : RepeatType.NEVER;
    }

    /**
     * Applies recurrence settings based on the selected repeat type.
     *
     * Normal Task
     * -----------
     * repeatType = NEVER
     * recurrenceMaster = false
     * recurrenceActive = true
     * masterTaskId = null
     * lastGeneratedDate = null
     *
     * Master Recurring Task
     * ---------------------
     * recurrenceMaster = true
     * recurrenceActive = true
     * masterTaskId = null
     * lastGeneratedDate = taskDate
     */
    private void applyRecurrenceSettings(Task task, RepeatType repeatType) {

        task.setRepeatType(resolveRepeatType(repeatType));

        if (task.getRepeatType() == RepeatType.NEVER) {

            task.setRecurrenceMaster(false);
            task.setRecurrenceActive(true);
            task.setMasterTaskId(null);
            task.setLastGeneratedDate(null);

        } else {

            task.setRecurrenceMaster(true);
            task.setRecurrenceActive(true);
            task.setMasterTaskId(null);

            // Scheduler will generate the next occurrence
            // starting from this date.
            task.setLastGeneratedDate(task.getTaskDate());
        }
    }

    /**
     * Maps a {@link Task} entity to its outward-facing {@link TaskResponse} DTO.
     * Note: recurrenceMaster is intentionally NOT exposed here — it is an
     * internal backend-only field used to identify the "master" task in a
     * recurrence series.
     */
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTaskName(),
                task.getDescription(),
                task.getTaskDate(),
                task.getTaskTime(),
                task.getPriority(),
                task.getCompleted(),
                task.getRepeatType(),
                task.getRecurrenceActive()
        );
    }

    /**
     * Maps a {@link Task} entity to a {@link TaskHistoryResponse} DTO.
     * Note: recurrenceMaster is intentionally NOT exposed here — see
     * {@link #toResponse(Task)}.
     */
    private TaskHistoryResponse toTaskHistoryResponse(Task task) {
        return new TaskHistoryResponse(
                task.getId(),
                task.getTaskName(),
                task.getDescription(),
                task.getTaskDate(),
                task.getTaskTime(),
                task.getPriority(),
                task.getCompleted(),
                task.getRepeatType(),
                task.getRecurrenceActive()
        );
    }
}