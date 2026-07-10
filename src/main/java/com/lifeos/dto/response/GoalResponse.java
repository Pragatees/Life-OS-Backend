package com.lifeos.dto.response;

import com.lifeos.entity.GoalStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class GoalResponse {

    private UUID id;

    private String goalName;

    private String description;

    private LocalDate goalDate;

    private LocalDate deadline;

    private GoalStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public GoalResponse() {
    }

    public GoalResponse(
            UUID id,
            String goalName,
            String description,
            LocalDate goalDate,
            LocalDate deadline,
            GoalStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.goalName = goalName;
        this.description = description;
        this.goalDate = goalDate;
        this.deadline = deadline;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}