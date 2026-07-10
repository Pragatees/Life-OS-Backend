package com.lifeos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateNoteRequest {

    @NotBlank(message = "Content is required")
    @Size(
            max = 5000,
            message = "Content cannot exceed 5000 characters"
    )
    private String content;

    @NotNull(message = "Note date is required")
    private LocalDate noteDate;

    public CreateNoteRequest() {
    }

    // ===========================
    // Getters & Setters
    // ===========================

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(LocalDate noteDate) {
        this.noteDate = noteDate;
    }
}