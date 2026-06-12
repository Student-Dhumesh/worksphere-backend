package com.worksphere.backend.workspace.dto;

import com.worksphere.backend.auth.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}
