package com.worksphere.backend.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String role;
    }
}
