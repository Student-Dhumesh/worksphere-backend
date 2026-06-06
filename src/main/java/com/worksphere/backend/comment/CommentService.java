package com.worksphere.backend.comment;

import com.worksphere.backend.auth.User;
import com.worksphere.backend.auth.UserRepository;
import com.worksphere.backend.comment.dto.CommentRequest;
import com.worksphere.backend.comment.dto.CommentResponse;
import com.worksphere.backend.comment.dto.CommentUpdateRequest;
import com.worksphere.backend.exception.AccessDeniedException;
import com.worksphere.backend.exception.ResourceNotFoundException;
import com.worksphere.backend.task.Task;
import com.worksphere.backend.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
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

//    Helper function - Map to response
    private CommentResponse mapToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .taskId(comment.getTask().getId())
                .authorName(comment.getAuthor().getName())
                .authorEmail(comment.getAuthor().getEmail())
                .build();
    }

//    Create comment
    public CommentResponse createComment(CommentRequest request) {
        User author = getCurrentUser();

        Task task = taskRepository
                .findById(request.getTaskId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .task(task)
                .author(author)
                .build();

        commentRepository.save(comment);

        return mapToResponse(comment);
    }

//    Get all comments by task
    public List<CommentResponse> getCommentsByTask(Long taskId) {

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task not found")
                );

        return commentRepository
                .findByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

//    Update comment
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request) {

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comment not found")
                );

        User currentUser = getCurrentUser();

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own comments");
        }

        comment.setContent(request.getContent());
        commentRepository.save(comment);

        return mapToResponse(comment);
    }

//    Delete comment
    public void deleteComment(Long commentId) throws RuntimeException {

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Comment not found")
                );

        User currentUser = getCurrentUser();

        if (!comment
                .getAuthor()
                .getId()
                .equals(currentUser.getId())
        ) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

}
