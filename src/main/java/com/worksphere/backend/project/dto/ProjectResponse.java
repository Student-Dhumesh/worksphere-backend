package com.worksphere.backend.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long workspaceId;
    private String workspaceName;
}
