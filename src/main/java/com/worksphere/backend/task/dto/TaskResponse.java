package com.worksphere.backend.task.dto;

import com.worksphere.backend.task.TaskPriority;
import com.worksphere.backend.task.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private Long projectId;
    private String projectName;
}
