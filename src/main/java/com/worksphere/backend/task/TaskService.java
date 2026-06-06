package com.worksphere.backend.task;

import com.worksphere.backend.auth.User;
import com.worksphere.backend.auth.UserRepository;
import com.worksphere.backend.exception.ResourceNotFoundException;
import com.worksphere.backend.project.Project;
import com.worksphere.backend.project.ProjectRepository;
import com.worksphere.backend.task.dto.TaskRequest;
import com.worksphere.backend.task.dto.TaskResponse;
import com.worksphere.backend.task.dto.TaskStatusUpdateRequest;
import com.worksphere.backend.task.dto.TaskUpdateRequest;
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

        if (!task
                .getProject()
                .getWorkspace()
                .getOwner()
                .getId()
                .equals(currentUser.getId())
        ) {
            throw new RuntimeException("Only the workspace owner can update tasks");
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

        if (!task
                .getProject()
                .getWorkspace()
                .getOwner()
                .getId()
                .equals(currentUser.getId())
        ) {
            throw new RuntimeException("Only the workspace owner can update task status");
        }

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

        if (!task
                .getProject()
                .getWorkspace()
                .getOwner()
                .getId()
                .equals(currentUser.getId())
        ) {
            throw new RuntimeException("Only the workspace owner can delete tasks");
        }

        taskRepository.delete(task);
    }

}
