package com.lifeos.dto.request;

import com.lifeos.entity.RepeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateTaskRequest {

    @NotBlank(message = "Task name is required")
    private String taskName;

    private String description;

    @NotNull(message = "Task date is required")
    private LocalDate taskDate;

    @NotNull(message = "Task time is required")
    private LocalTime taskTime;

    @NotBlank(message = "Priority is required")
    private String priority;

    /**
     * Optional.
     * Defaults to NEVER if not provided.
     */
    private RepeatType repeatType = RepeatType.NEVER;

    public UpdateTaskRequest() {
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

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType repeatType) {
        this.repeatType = (repeatType != null) ? repeatType : RepeatType.NEVER;
    }
}