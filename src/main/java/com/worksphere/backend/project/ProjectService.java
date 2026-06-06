package com.worksphere.backend.project;

import com.worksphere.backend.auth.User;
import com.worksphere.backend.auth.UserRepository;
import com.worksphere.backend.exception.ResourceNotFoundException;
import com.worksphere.backend.project.dto.ProjectRequest;
import com.worksphere.backend.project.dto.ProjectResponse;
import com.worksphere.backend.workspace.Workspace;
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

        if (!project
                .getWorkspace()
                .getOwner()
                .getId()
                .equals(currentUser.getId())
        ) {
            throw new RuntimeException("Only the workspace owner can update projects");
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

        if (!project
                .getWorkspace()
                .getOwner()
                .getId()
                .equals(currentUser.getId())
        ) {
            throw new RuntimeException("Only the workspace owner can delete projects");
        }

        projectRepository.delete(project);
    }

}
