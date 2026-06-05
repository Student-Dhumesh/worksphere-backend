package com.worksphere.backend.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
