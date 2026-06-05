package com.worksphere.backend.comment;

import com.worksphere.backend.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtAsc(Task task);
}
