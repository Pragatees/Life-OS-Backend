package com.lifeos.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateGoalRequest {

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

    @NotNull(message = "Goal date is required")
    private LocalDate goalDate;

    @NotNull(message = "Deadline is required")
    @FutureOrPresent(message = "Deadline cannot be in the past")
    private LocalDate deadline;

    public CreateGoalRequest() {
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

    public LocalDate getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(LocalDate goalDate) {
        this.goalDate = goalDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}