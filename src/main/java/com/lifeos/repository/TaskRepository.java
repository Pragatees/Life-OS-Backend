package com.lifeos.repository;

import com.lifeos.entity.Task;
import com.lifeos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    /*
     * Get all tasks of a user for a particular date
     */
    List<Task> findByUserAndTaskDateOrderByTaskTimeAsc(
            User user,
            LocalDate taskDate
    );

    /*
     * Get a task only if it belongs to the user
     */
    Optional<Task> findByIdAndUser(
            UUID id,
            User user
    );

    /*
     * Delete all tasks belonging to a user
     */
    void deleteByUser(User user);
}