package com.lifeos.controller;

import com.lifeos.dto.request.CreateGoalRequest;
import com.lifeos.dto.request.UpdateGoalRequest;
import com.lifeos.dto.response.GoalResponse;
import com.lifeos.dto.response.MessageResponse;
import com.lifeos.entity.Goal;
import com.lifeos.entity.User;
import com.lifeos.exception.GoalNotFoundException;
import com.lifeos.exception.UserNotFoundException;
import com.lifeos.repository.GoalRepository;
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

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalController(
            GoalRepository goalRepository,
            UserRepository userRepository
    ) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    // -------------------------------------------------------------------
    // POST /api/goals
    // Create a new goal for the authenticated user.
    // -------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @Valid @RequestBody CreateGoalRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {

        User user = getUserOrThrow(userPrincipal);

        if (request.getDeadline().isBefore(request.getGoalDate())) {
            return ResponseEntity.badRequest().build();
        }

        Goal goal = new Goal();

        goal.setGoalName(request.getGoalName());
        goal.setDescription(request.getDescription());
        goal.setGoalDate(request.getGoalDate());
        goal.setDeadline(request.getDeadline());
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedGoal));
    }

    // -------------------------------------------------------------------
    // GET /api/goals
    // Return all goals of the authenticated user,
    // ordered by creation date (latest first).
    // -------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<GoalResponse>> getAllGoals(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        List<Goal> goals =
                goalRepository.findByUserOrderByCreatedAtDesc(user);

        List<GoalResponse> response = goals.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------
    // GET /api/goals/date?date=2026-07-10
    // Return all goals created for a particular date.
    // -------------------------------------------------------------------
    @GetMapping("/date")
    public ResponseEntity<List<GoalResponse>> getGoalsByDate(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        List<Goal> goals =
                goalRepository.findByUserAndGoalDateOrderByCreatedAtDesc(
                        user,
                        date
                );

        List<GoalResponse> response = goals.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // -------------------------------------------------------------------
    // GET /api/goals/{goalId}
    // Return a single goal of the authenticated user.
    // -------------------------------------------------------------------
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> getGoalById(
            @PathVariable UUID goalId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        Goal goal = getGoalOrThrow(goalId, user);

        return ResponseEntity.ok(
                toResponse(goal)
        );
    }

    // -------------------------------------------------------------------
    // PUT /api/goals/{goalId}
    // Update an existing goal.
    // -------------------------------------------------------------------
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable UUID goalId,
            @Valid @RequestBody UpdateGoalRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        Goal goal = getGoalOrThrow(goalId, user);

        // Validate deadline
        if (request.getDeadline().isBefore(goal.getGoalDate())) {
            // Method return type is ResponseEntity<GoalResponse>, so the
            // error branch must not return a different body type
            // (e.g. MessageResponse) or generic inference fails at compile time.
            return ResponseEntity.badRequest().build();
        }

        goal.setGoalName(request.getGoalName());
        goal.setDescription(request.getDescription());
        goal.setDeadline(request.getDeadline());
        goal.setStatus(request.getStatus());

        Goal updatedGoal = goalRepository.save(goal);

        return ResponseEntity.ok(
                toResponse(updatedGoal)
        );
    }

    // -------------------------------------------------------------------
    // DELETE /api/goals/{goalId}
    // Permanently delete a goal.
    // -------------------------------------------------------------------
    @DeleteMapping("/{goalId}")
    public ResponseEntity<MessageResponse> deleteGoal(
            @PathVariable UUID goalId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        Goal goal = getGoalOrThrow(goalId, user);

        goalRepository.delete(goal);

        return ResponseEntity.ok(
                new MessageResponse("Goal deleted successfully")
        );
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

    /**
     * Resolve the authenticated user.
     */
    private User getUserOrThrow(UserPrincipal userPrincipal) {

        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    /**
     * Resolve a goal belonging to the authenticated user.
     */
    private Goal getGoalOrThrow(
            UUID goalId,
            User user) {

        return goalRepository.findByIdAndUser(
                        goalId,
                        user
                )
                .orElseThrow(() ->
                        new GoalNotFoundException("Goal not found"));
    }

    /**
     * Convert Goal Entity → GoalResponse DTO.
     */
    private GoalResponse toResponse(Goal goal) {

        return new GoalResponse(
                goal.getId(),
                goal.getGoalName(),
                goal.getDescription(),
                goal.getGoalDate(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }

}
