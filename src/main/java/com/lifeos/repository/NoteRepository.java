package com.lifeos.repository;

import com.lifeos.entity.Note;
import com.lifeos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    /*
     * Get the note of a user for a particular date.
     */
    Optional<Note> findByUserAndNoteDate(
            User user,
            LocalDate noteDate
    );

    /*
     * Get a note only if it belongs to the user.
     */
    Optional<Note> findByIdAndUser(
            UUID id,
            User user
    );

    /*
     * Check whether a note already exists for a date.
     */
    boolean existsByUserAndNoteDate(
            User user,
            LocalDate noteDate
    );

    /*
     * Returns all dates for which the authenticated user
     * has created notes.
     */
    @Query("""
            SELECT n.noteDate
            FROM Note n
            WHERE n.user = :user
            ORDER BY n.noteDate ASC
            """)
    List<LocalDate> findAllNoteDatesByUser(
            @Param("user") User user
    );

    /*
     * Delete all notes belonging to a user.
     * Useful when deleting an account.
     */
    void deleteByUser(User user);
}