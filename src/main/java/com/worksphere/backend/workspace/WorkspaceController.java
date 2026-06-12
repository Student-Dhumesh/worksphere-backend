package com.worksphere.backend.workspace;

import com.worksphere.backend.workspace.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@Tag(name = "Workspace", description = "Workspace management")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workspaceService.createWorkspace(request));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAllMyWorkspaces() {
        return ResponseEntity.ok(
                workspaceService.getAllMyWorkspaces()
        );
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(workspaceService.getWorkspace(workspaceId));
    }

    @GetMapping("/joined")
    public ResponseEntity<List<WorkspaceResponse>> getJoinedWorkspaces() {
        return ResponseEntity.ok(
                workspaceService.getJoinedWorkspaces()
        );
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(@PathVariable Long workspaceId, @Valid @RequestBody WorkspaceRequest request) {
        return ResponseEntity.ok(
                workspaceService.updateWorkspace(workspaceId, request)
        );
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deletedWorkspace(@PathVariable Long workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<MemberResponse> addMember (@PathVariable Long workspaceId, @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(workspaceService.addMember(workspaceId, request));
    }

    @PatchMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long workspaceId, @PathVariable Long userId, @Valid @RequestBody UpdateMemberRoleRequest request) {
        return ResponseEntity.ok(
                workspaceService.updateMemberRole(workspaceId, userId, request)
        );
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long workspaceId, @PathVariable Long userId) {
        workspaceService.removeMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

}
