package com.worksphere.backend.project;

import com.worksphere.backend.auth.Role;
import com.worksphere.backend.auth.User;
import com.worksphere.backend.auth.UserRepository;
import com.worksphere.backend.exception.ResourceNotFoundException;
import com.worksphere.backend.project.dto.ProjectRequest;
import com.worksphere.backend.project.dto.ProjectResponse;
import com.worksphere.backend.workspace.Workspace;
import com.worksphere.backend.workspace.WorkspaceMember;
import com.worksphere.backend.workspace.WorkspaceMemberRepository;
import com.worksphere.backend.workspace.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

//    Helper Function - Get current logged in user
    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(("User not found"))
                );
    }

//    Helper Function - Get current user's membership in a workspace
    private WorkspaceMember getMembership(Workspace workspace, User user) {
        if (workspace
                .getOwner()
                .getId()
                .equals(user.getId())
        ) {
            return null;
        }

        return workspaceMemberRepository
                .findByWorkspaceAndUser(workspace, user)
                .orElseThrow(() ->
                        new RuntimeException("You are not a member of this workspace")
                );
    }

    //    Helper Function - Check if user is owner
    private boolean isOwner(Workspace workspace, User user) {
        return workspace.getOwner().getId().equals(user.getId());
    }

//    Helper Function - Check if user is owner or manager
    private boolean isOwnerOrManager(Workspace workspace, User user) {
        if (isOwner(workspace, user)) return true;

        return workspaceMemberRepository
                .findByWorkspaceAndUser(workspace, user)
                .map(member -> member.getRole() == Role.MANAGER)
                .orElse(false);
    }

//    Helper Function - Map to response
    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .workspaceId(project.getWorkspace().getId())
                .workspaceName(project.getWorkspace().getName())
                .build();
    }

//    Create project
    public ProjectResponse createProject(ProjectRequest request) {

        Workspace workspace = workspaceRepository
                .findById(request.getWorkspaceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User currentUser = getCurrentUser();

        if (!isOwnerOrManager(workspace, currentUser)) {
            throw new RuntimeException("Only owner or manager can create projects");
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .workspace(workspace)
                .build();

        projectRepository.save(project);

        return mapToResponse(project);
    }

//    Get all projects by workspace
    public List<ProjectResponse> getProjectsByWorkspace(Long workspaceId) {

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User currentUser = getCurrentUser();

        getMembership(workspace, currentUser);

        return projectRepository.findByWorkspace(workspace)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    Get project by id
    public ProjectResponse getProject(Long projectId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        return mapToResponse(project);
    }

//    Update project
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        User currentUser = getCurrentUser();
        Workspace workspace = project.getWorkspace();

        if (!isOwnerOrManager(workspace, currentUser)) {
            throw new RuntimeException("Only owner or manager can update projects");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        projectRepository.save(project);
        return mapToResponse(project);
    }

//    Delete project
    public void deleteProject(Long projectId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        User currentUser = getCurrentUser();
        Workspace workspace = project.getWorkspace();

        if (!isOwnerOrManager(workspace, currentUser)) {
            throw new RuntimeException("Only the owner can delete projects");
        }

        projectRepository.delete(project);
    }

}
