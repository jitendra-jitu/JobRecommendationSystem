package com.techstart.jobportal.repository;

import com.techstart.jobportal.model.SearchHistory;
import com.techstart.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUser(User user);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM SearchHistory sh WHERE sh.user = :user AND sh.timestamp >= :startTime")
//    void deleteByUserAndTimestampAfter(User user, LocalDateTime startTime);
//
//    @Transactional
//    @Modifying
//    @Query("DELETE FROM SearchHistory sh WHERE sh.user = :user")
//    void deleteByUser(User user);

    // Delete search history entries for a user after a given timestamp
    int deleteByUserAndTimestampAfter(User user, LocalDateTime timestamp);

    // Delete all search history entries for a user
    int deleteByUser(User user);

}
