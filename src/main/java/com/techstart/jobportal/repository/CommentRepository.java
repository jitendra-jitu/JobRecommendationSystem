
package com.techstart.jobportal.repository;

import com.techstart.jobportal.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
