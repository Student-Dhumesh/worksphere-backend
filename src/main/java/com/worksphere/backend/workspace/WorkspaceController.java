package com.worksphere.backend.workspace;

import com.worksphere.backend.workspace.dto.AddMemberRequest;
import com.worksphere.backend.workspace.dto.MemberResponse;
import com.worksphere.backend.workspace.dto.WorkspaceRequest;
import com.worksphere.backend.workspace.dto.WorkspaceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
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
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces() {
        return ResponseEntity.ok(workspaceService.getMyWorkspaces());
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(workspaceService.getWorkspace(workspaceId));
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

    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long workspaceId, @PathVariable Long userId) {
        workspaceService.removeMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

}
