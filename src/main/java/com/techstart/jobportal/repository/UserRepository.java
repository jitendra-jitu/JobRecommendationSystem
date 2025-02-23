package com.techstart.jobportal.repository;

import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT j FROM Job j WHERE " +
            "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "j.id NOT IN :appliedJobIds")
    Page<Job> findByKeywordsAndNotApplied(@Param("keywords") List<String> keywords,
                                          @Param("appliedJobIds") List<Long> appliedJobIds,
                                          Pageable pageable);



    Optional<User> findByUsername(String username);
}