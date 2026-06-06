package com.worksphere.backend.project;

import com.worksphere.backend.project.dto.ProjectRequest;
import com.worksphere.backend.project.dto.ProjectResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "Project management")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectService.createProject(request));
    }

    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<ProjectResponse>> getByWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(
                projectService.getProjectsByWorkspace(workspaceId)
        );
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(
                projectService.getProject(projectId)
        );
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(
                projectService.updateProject(projectId, request)
        );
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

}
