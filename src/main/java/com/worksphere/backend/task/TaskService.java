package com.worksphere.backend.task;

import com.worksphere.backend.auth.Role;
import com.worksphere.backend.auth.User;
import com.worksphere.backend.auth.UserRepository;
import com.worksphere.backend.exception.ResourceNotFoundException;
import com.worksphere.backend.project.Project;
import com.worksphere.backend.project.ProjectRepository;
import com.worksphere.backend.task.dto.TaskRequest;
import com.worksphere.backend.task.dto.TaskResponse;
import com.worksphere.backend.task.dto.TaskStatusUpdateRequest;
import com.worksphere.backend.task.dto.TaskUpdateRequest;
import com.worksphere.backend.workspace.Workspace;
import com.worksphere.backend.workspace.WorkspaceMember;
import com.worksphere.backend.workspace.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
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
                        new ResourceNotFoundException("User not found")
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
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .build();
    }

//    Create task
    public TaskResponse createTask(TaskRequest request) {

        Project project = projectRepository
                .findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        User currentUser = getCurrentUser();
        Workspace workspace = project.getWorkspace();

        getMembership(workspace, currentUser);

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .project(project)
                .build();

        taskRepository.save(task);

        return mapToResponse(task);
    }

//    Get all tasks by project
    public List<TaskResponse> getTaskByProject(Long projectId) {

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        return taskRepository.findByProject(project)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    Get all tasks by project and status
    public List<TaskResponse> getTasksByProjectAndStatus(Long projectId, TaskStatus status) {

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found")
                );

        return taskRepository.findByProjectAndStatus(project, status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    Get task by task id
    public TaskResponse getTask(Long taskId) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        return mapToResponse(task);
    }

//    Update task
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        User currentUser = getCurrentUser();
        Workspace workspace = task.getProject().getWorkspace();

        if (!isOwnerOrManager(workspace, currentUser)) {
            throw new RuntimeException("Only owner or manager can update tasks");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        taskRepository.save(task);

        return mapToResponse(task);
    }

//    Update task status
    public TaskResponse updateTaskStatus(Long taskId, TaskStatusUpdateRequest request) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        User currentUser = getCurrentUser();
        Workspace workspace = task.getProject().getWorkspace();

        getMembership(workspace, currentUser);

        task.setStatus(request.getStatus());

        taskRepository.save(task);

        return mapToResponse(task);
    }

//    Delete task
    public void deleteTask(Long taskId) {
        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        User currentUser = getCurrentUser();
        Workspace workspace = task.getProject().getWorkspace();

        if (!isOwnerOrManager(workspace, currentUser)) {
            throw new RuntimeException("Only owner or manager can delete tasks");
        }

        taskRepository.delete(task);
    }

}
