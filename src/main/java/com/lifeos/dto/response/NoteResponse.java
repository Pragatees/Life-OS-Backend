package com.lifeos.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class NoteResponse {

    private UUID id;

    private String content;

    private LocalDate noteDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public NoteResponse() {
    }

    public NoteResponse(
            UUID id,
            String content,
            LocalDate noteDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.content = content;
        this.noteDate = noteDate;
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