package com.techstart.jobportal.repository;

import com.techstart.jobportal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {




    @Query("SELECT j FROM Job j WHERE j.id <> :jobId AND (j.title LIKE %:title% OR j.description LIKE %:description%)")
    List<Job> findSimilarJobs(@Param("jobId") Long jobId, @Param("title") String title, @Param("description") String description);


    @Query("SELECT j FROM Job j WHERE j.title LIKE %:keyword% OR j.description LIKE %:keyword%")
    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword, String s);

    @Query("SELECT j FROM Job j WHERE j.id <> :jobId AND (j.title LIKE %:keyword% OR j.description LIKE %:keyword%)")
    List<Job> findSimilarJobs(@Param("jobId") Long jobId);


    List<Job> findByIdNotIn(List<Long> interactedIds);

    @Query("SELECT j FROM Job j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(j.company) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(j.location) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Job> searchJobs(@Param("query") String query, Pageable pageable);

    Page<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE (LOWER(j.title) LIKE LOWER(concat('%', :keyword, '%')) OR LOWER(j.description) LIKE LOWER(concat('%', :keyword, '%'))) AND j.id NOT IN :appliedJobIds")
    Page<Job> findByKeywordsAndNotApplied(
            @Param("keyword") String keyword,
            @Param("appliedJobIds") List<Long> appliedJobIds,
            Pageable pageable
    );

    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrRequiredSkillsContainingIgnoreCaseOrCompanyContainingIgnoreCaseOrLocationContainingIgnoreCase(String query, String query1, String query2, String query3, String query4);


    @Query("SELECT DISTINCT j FROM Job j " +
            "LEFT JOIN j.requiredSkills rs " +
            "WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(rs) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(j.company) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(j.location) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(j.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Job> searchByAllFields(@Param("query") String query);



}