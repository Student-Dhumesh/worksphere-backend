package com.worksphere.backend.workspace;

import com.worksphere.backend.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByOwner(User owner);
    List<Workspace> findByMembersUser(User user);
}
