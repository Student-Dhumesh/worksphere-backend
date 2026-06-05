package com.worksphere.backend.workspace.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
}
