package com.worksphere.backend.workspace.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WorkspaceResponse {

    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private List<MemberResponse> members;
}
