package com.worksphere.backend.task.dto;

import com.worksphere.backend.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskPriority priority = TaskPriority.MEDIUM;

    @NotNull(message = "Project id is required")
    private Long projectId;
}
