package com.lifeos.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private String taskName;
    private String description;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private String priority;
    private Boolean completed;

    public TaskResponse() {
    }

    public TaskResponse(UUID id,
                        String taskName,
                        String description,
                        LocalDate taskDate,
                        LocalTime taskTime,
                        String priority,
                        Boolean completed) {

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
}