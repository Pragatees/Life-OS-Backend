package com.lifeos.repository;

import com.lifeos.entity.Task;
import com.lifeos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // ============================================================
    // Standard Task Queries
    // ============================================================

    /**
     * Get all tasks of a user for a particular date.
     */
    List<Task> findByUserAndTaskDateOrderByTaskTimeAsc(
            User user,
            LocalDate taskDate
    );

    /**
     * Get all tasks of a user between two dates.
     */
    List<Task> findByUserAndTaskDateBetweenOrderByTaskDateAscTaskTimeAsc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Get a task only if it belongs to the user.
     */
    Optional<Task> findByIdAndUser(
            UUID id,
            User user
    );

    /**
     * Delete all tasks belonging to a user.
     */
    void deleteByUser(User user);

    // ============================================================
    // Master Recurrence Queries
    // ============================================================

    /**
     * Returns every active recurring master task.
     *
     * Only these tasks are processed by the recurrence scheduler.
     */
    List<Task> findByRecurrenceMasterTrueAndRecurrenceActiveTrue();

    /**
     * Returns all occurrences belonging to a master task.
     */
    List<Task> findByMasterTaskIdOrderByTaskDateAscTaskTimeAsc(
            UUID masterTaskId
    );

    /**
     * Checks whether an occurrence already exists for a master task
     * on a particular date.
     *
     * Prevents duplicate occurrence generation.
     */
    boolean existsByMasterTaskIdAndTaskDate(
            UUID masterTaskId,
            LocalDate taskDate
    );

    /**
     * Returns all future occurrences of a master task.
     *
     * This will be used later for:
     * - Edit all future occurrences
     * - Delete all future occurrences
     */
    List<Task> findByMasterTaskIdAndTaskDateGreaterThanEqualOrderByTaskDateAscTaskTimeAsc(
            UUID masterTaskId,
            LocalDate taskDate
    );
}