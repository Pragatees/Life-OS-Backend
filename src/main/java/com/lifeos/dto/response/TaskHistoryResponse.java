package com.lifeos.dto.response;

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

    public TaskHistoryResponse() {
    }

    public TaskHistoryResponse(
            UUID id,
            String taskName,
            String description,
            LocalDate taskDate,
            LocalTime taskTime,
            String priority,
            Boolean completed
    ) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.priority = priority;
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(LocalDate taskDate) {
        this.taskDate = taskDate;
    }

    public LocalTime getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(LocalTime taskTime) {
        this.taskTime = taskTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}