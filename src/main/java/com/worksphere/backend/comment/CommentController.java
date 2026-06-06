package com.worksphere.backend.comment;

import com.worksphere.backend.comment.dto.CommentRequest;
import com.worksphere.backend.comment.dto.CommentResponse;
import com.worksphere.backend.comment.dto.CommentUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        commentService.createComment(request)
                );
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponse>> getByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(
                commentService.getCommentsByTask(taskId)
        );
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(
                commentService.updateComment(commentId, request)
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

}
