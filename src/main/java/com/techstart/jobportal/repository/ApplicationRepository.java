package com.techstart.jobportal.repository;

import com.techstart.jobportal.model.Application;
import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);

    // New: Find a specific application by User and Job ID
    Optional<Application> findByUserAndJobId(User user, Long jobId);

    boolean existsByUserAndJob(User user, Job job);
}
