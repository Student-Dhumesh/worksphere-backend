package com.worksphere.backend.workspace;

import com.worksphere.backend.auth.User;
import com.worksphere.backend.auth.UserRepository;
import com.worksphere.backend.exception.AccessDeniedException;
import com.worksphere.backend.exception.AlreadyExistsException;
import com.worksphere.backend.exception.ResourceNotFoundException;
import com.worksphere.backend.workspace.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
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

//    Helper Function - Check if user is owner
    private boolean isOwner(Workspace workspace, User user) {
        return workspace.getOwner().getId().equals(user.getId());
    }

//    Helper Function - Map to response
    private WorkspaceResponse mapToResponse(Workspace workspace) {
        List<MemberResponse> members = workspace.getMembers()
                .stream()
                .map(member -> MemberResponse.builder()
                        .id(member.getUser().getId())
                        .name(member.getUser().getName())
                        .email(member.getUser().getEmail())
                        .role(member.getRole().name())
                        .build()
                )
                .toList();

        return WorkspaceResponse.builder()
                .id(workspace.getId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .ownerEmail(workspace.getOwner().getEmail())
                .members(members)
                .build();
    }

//    Create workspace
    public WorkspaceResponse createWorkspace(WorkspaceRequest request) {

        User owner = getCurrentUser();

        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        workspaceRepository.save(workspace);
        return mapToResponse(workspace);
    }

//    Get all workspaces
    public List<WorkspaceResponse> getMyWorkspaces() {
        User owner = getCurrentUser();

        return workspaceRepository.findByOwner(owner)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    Get workspace by id
    public WorkspaceResponse getWorkspace(Long workspaceId) {

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Workspace not found")
                );

        return mapToResponse(workspace);
    }

//    Get joined workspace
    public List<WorkspaceResponse> getJoinedWorkspaces() {
        User currentUser = getCurrentUser();

        return workspaceRepository
                .findByMembersUser(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    Get all workspaces together (owned + joined)
    public List<WorkspaceResponse> getAllMyWorkspaces() {

        User currentUser = getCurrentUser();

        List<Workspace> owned = workspaceRepository
                .findByOwner(currentUser);

        List<Workspace> joined = workspaceRepository
                .findByMembersUser(currentUser);

        return Stream.concat(owned.stream(), joined.stream())
                .distinct()
                .map(this::mapToResponse)
                .toList();
    }

//    Update workspace
    public WorkspaceResponse updateWorkspace(Long workspaceId, WorkspaceRequest request) {

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User currentUser = getCurrentUser();

        if (!isOwner(workspace, currentUser)) {
            throw new AccessDeniedException("Only the owner can update this workspace");
        }

        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());

        workspaceRepository.save(workspace);

        return mapToResponse(workspace);
    }

//    Update role
    public MemberResponse updateMemberRole(Long workspaceId, Long userId, UpdateMemberRoleRequest request) {

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User currentUser = getCurrentUser();

        if(!isOwner(workspace, currentUser)) {
            throw new AccessDeniedException("Only the owner can update member roles");
        }

        User userToUpdate = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceAndUser(workspace, userToUpdate)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found")
                );

        member.setRole(request.getRole());

        workspaceMemberRepository.save(member);

        return MemberResponse.builder()
                .id(userToUpdate.getId())
                .name(userToUpdate.getName())
                .email(userToUpdate.getEmail())
                .role(member.getRole().name())
                .build();
    }

//    Delete workspace
    public void deleteWorkspace(Long workspaceId) {
        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User currentUser = getCurrentUser();

        if (!workspace.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(
                    "Only the owner can delete this workspace"
            );
        }

        workspaceRepository.delete(workspace);
    }

//    Add member
    public MemberResponse addMember(Long workspaceId, AddMemberRequest request) {

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User userToAdd = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (workspaceMemberRepository.existsByWorkspaceAndUser(workspace, userToAdd)) {
            throw new AlreadyExistsException("User is already a member");
        }

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(userToAdd)
                .role(request.getRole())
                .build();

        workspaceMemberRepository.save(member);

        return MemberResponse.builder()
                .id(userToAdd.getId())
                .name(userToAdd.getName())
                .email(userToAdd.getEmail())
                .role(member.getRole().name())
                .build();
    }

//    Remove member
    public void removeMember(Long workspaceId, Long userId) {

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found")
                );

        User currentUser = getCurrentUser();

        if (!workspace.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the owner can remove members");
        }

        User userToRemove = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceAndUser(workspace, userToRemove)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found")
                );

        workspaceMemberRepository.delete(member);
    }

}
