package com.worksphere.backend.workspace;

import com.worksphere.backend.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {
    Optional<WorkspaceMember> findByWorkspaceAndUser(Workspace workspace, User user);

    boolean existsByWorkspaceAndUser(Workspace workspace, User user);
}
