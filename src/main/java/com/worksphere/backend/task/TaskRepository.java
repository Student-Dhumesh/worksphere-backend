package com.worksphere.backend.task;

import com.worksphere.backend.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByProjectAndStatus(Project project, TaskStatus status);
}
