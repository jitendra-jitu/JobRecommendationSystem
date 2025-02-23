package com.techstart.jobportal.repository;

import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.User;
import com.techstart.jobportal.model.UserInteraction;
import com.techstart.jobportal.model.InteractionType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {


    @Transactional
    void deleteByUser(User user);

    // Delete all user interactions (for global reset)
    @Transactional
    void deleteAll();


    List<UserInteraction> findByUser(User user);

    // Find users who have interacted with the given job IDs
    @Query("SELECT DISTINCT ui.user.id FROM UserInteraction ui WHERE ui.job.id IN :jobIds")
    Set<Long> findUsersByJobIds(@Param("jobIds") Set<Long> jobIds);

    // Check if a user has interacted with a specific job
    boolean existsByUserAndJob(User user, Job job);

    List<UserInteraction> findByUserAndType(User user, InteractionType interactionType);

    List<UserInteraction> findTop10ByUserOrderByTimestampDesc(User user);

    void deleteByUserAndJobAndType(User user, Job job, InteractionType interactionType);

    // Delete interactions of a specific type for a user after a given timestamp
    int deleteByUserAndTypeAndTimestampAfter(User user, InteractionType type, LocalDateTime timestamp);

    // Delete all interactions of a specific type for a user
    int deleteByUserAndType(User user, InteractionType type);
}