package com.lifeos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateNoteRequest {

    @NotBlank(message = "Content is required")
    @Size(
            max = 5000,
            message = "Content cannot exceed 5000 characters"
    )
    private String content;

    public UpdateNoteRequest() {
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
}