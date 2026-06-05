package com.worksphere.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Task id is required")
    private long taskId;
}
