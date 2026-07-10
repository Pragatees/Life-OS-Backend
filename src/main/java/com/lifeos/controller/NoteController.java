package com.lifeos.controller;

import com.lifeos.dto.request.CreateNoteRequest;
import com.lifeos.dto.request.UpdateNoteRequest;
import com.lifeos.dto.response.MessageResponse;
import com.lifeos.dto.response.NoteResponse;
import com.lifeos.entity.Note;
import com.lifeos.entity.User;
import com.lifeos.exception.NoteNotFoundException;
import com.lifeos.exception.UserNotFoundException;
import com.lifeos.repository.NoteRepository;
import com.lifeos.repository.UserRepository;
import com.lifeos.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST controller for managing a user's daily notes.
 *
 * Base Path:
 * /api/notes
 *
 * Endpoints:
 *
 * POST    /api/notes
 * GET     /api/notes/date
 * PUT     /api/notes/{noteId}
 * DELETE  /api/notes/{noteId}
 *
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteController(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    // -------------------------------------------------------------------
    // POST /api/notes
    // Create a note for the authenticated user.
    // One note is allowed per date.
    // -------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<?> createNote(
            @Valid @RequestBody CreateNoteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        if (noteRepository.existsByUserAndNoteDate(user, request.getNoteDate())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("A note already exists for this date"));
        }

        Note note = new Note();
        note.setContent(request.getContent());
        note.setNoteDate(request.getNoteDate());
        note.setUser(user);

        Note savedNote = noteRepository.save(note);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(savedNote));
    }

    // -------------------------------------------------------------------
    // GET /api/notes/date?date=2026-07-10
    // Get a user's note for a particular date.
    // -------------------------------------------------------------------
    @GetMapping("/date")
    public ResponseEntity<NoteResponse> getNoteByDate(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);

        Note note = noteRepository
                .findByUserAndNoteDate(user, date)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));

        return ResponseEntity.ok(toResponse(note));
    }

    // -------------------------------------------------------------------
    // PUT /api/notes/{noteId}
    // Update an existing note.
    // -------------------------------------------------------------------
    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable UUID noteId,
            @Valid @RequestBody UpdateNoteRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);
        Note note = getNoteOrThrow(noteId, user);

        note.setContent(request.getContent());

        Note updatedNote = noteRepository.save(note);

        return ResponseEntity.ok(toResponse(updatedNote));
    }

    // -------------------------------------------------------------------
    // DELETE /api/notes/{noteId}
    // Permanently delete a note.
    // -------------------------------------------------------------------
    @DeleteMapping("/{noteId}")
    public ResponseEntity<MessageResponse> deleteNote(
            @PathVariable UUID noteId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        User user = getUserOrThrow(userPrincipal);
        Note note = getNoteOrThrow(noteId, user);

        noteRepository.delete(note);

        return ResponseEntity.ok(new MessageResponse("Note deleted successfully"));
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

    /**
     * Resolve the authenticated user.
     */
    private User getUserOrThrow(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Resolve a note belonging to the authenticated user.
     */
    private Note getNoteOrThrow(UUID noteId, User user) {
        return noteRepository.findByIdAndUser(noteId, user)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    }

    /**
     * Convert Note Entity → NoteResponse DTO.
     */
    private NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getContent(),
                note.getNoteDate(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}