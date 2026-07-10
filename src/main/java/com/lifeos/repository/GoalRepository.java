package com.lifeos.repository;

import com.lifeos.entity.Goal;
import com.lifeos.entity.GoalStatus;
import com.lifeos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    /*
     * Get all goals of a user for a particular date.
     */
    List<Goal> findByUserAndGoalDateOrderByCreatedAtDesc(
            User user,
            LocalDate goalDate
    );

    /*
     * Get a goal only if it belongs to the user.
     */
    Optional<Goal> findByIdAndUser(
            UUID id,
            User user
    );

    /*
     * Get all goals of a user ordered by creation date.
     */
    List<Goal> findByUserOrderByCreatedAtDesc(
            User user
    );

    /*
     * Get all goals by status.
     */
    List<Goal> findByUserAndStatusOrderByCreatedAtDesc(
            User user,
            GoalStatus status
    );

    /*
     * Delete all goals belonging to a user.
     */
    void deleteByUser(User user);

}