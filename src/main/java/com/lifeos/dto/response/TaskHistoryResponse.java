package com.lifeos.dto.response;

import com.lifeos.entity.RepeatType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TaskHistoryResponse {

    private UUID id;
    private String taskName;
    private String description;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private String priority;
    private Boolean completed;

    /**
     * Recurrence Information
     */
    private RepeatType repeatType;
    private Boolean recurrenceActive;

    public TaskHistoryResponse() {
    }

    public TaskHistoryResponse(
            UUID id,
            String taskName,
            String description,
            LocalDate taskDate,
            LocalTime taskTime,
            String priority,
            Boolean completed,
            RepeatType repeatType,
            Boolean recurrenceActive
    ) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.priority = priority;
        this.completed = completed;
        this.repeatType = repeatType;
        this.recurrenceActive = recurrenceActive;
    }

    // =====================================================
    // Getters
    // =====================================================

    public UUID getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public LocalTime getTaskTime() {
        return taskTime;
    }

    public String getPriority() {
        return priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public Boolean getRecurrenceActive() {
        return recurrenceActive;
    }

    // =====================================================
    // Setters
    // =====================================================

    public void setId(UUID id) {
        this.id = id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate;
    }

    public void setTaskTime(LocalTime taskTime) {
        this.taskTime = taskTime;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public void setRecurrenceActive(Boolean recurrenceActive) {
        this.recurrenceActive = recurrenceActive;
    }
}