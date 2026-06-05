package com.worksphere.backend.project;

import com.worksphere.backend.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByWorkspace(Workspace workspace);
}
