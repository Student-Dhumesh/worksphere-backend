package com.worksphere.backend.comment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long taskId;
    private String authorName;
    private String authorEmail;
}
