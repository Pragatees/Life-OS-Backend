package com.lifeos.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "note_date"
                        }
                )
        }
)
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            nullable = false,
            length = 5000
    )
    private String content;

    @Column(
            name = "note_date",
            nullable = false
    )
    private LocalDate noteDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    public Note() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}