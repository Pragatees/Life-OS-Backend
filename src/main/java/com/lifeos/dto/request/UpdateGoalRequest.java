package com.lifeos.dto.request;

import com.lifeos.entity.GoalStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateGoalRequest {

    @NotBlank(message = "Goal name is required")
    @Size(
            max = 150,
            message = "Goal name cannot exceed 150 characters"
    )
    private String goalName;

    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    private String description;

    @NotNull(message = "Deadline is required")
    @FutureOrPresent(message = "Deadline cannot be in the past")
    private LocalDate deadline;

    @NotNull(message = "Goal status is required")
    private GoalStatus status;

    public UpdateGoalRequest() {
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }
}