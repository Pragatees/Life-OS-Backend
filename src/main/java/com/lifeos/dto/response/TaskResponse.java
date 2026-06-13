package com.lifeos.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private String taskName;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private Boolean completed;

    public TaskResponse() {
    }

    public TaskResponse(UUID id,
                        String taskName,
                        LocalDate taskDate,
                        LocalTime taskTime,
                        Boolean completed) {

        this.id = id;
        this.taskName = taskName;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public LocalDate getTaskDate() {
        return taskDate;
    }

    public LocalTime getTaskTime() {
        return taskTime;
    }

    public Boolean getCompleted() {
        return completed;
    }
}