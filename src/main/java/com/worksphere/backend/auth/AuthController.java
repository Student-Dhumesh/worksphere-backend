package com.worksphere.backend.auth;

import com.worksphere.backend.auth.dto.AuthResponse;
import com.worksphere.backend.auth.dto.LoginRequest;
import com.worksphere.backend.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        String refreshToken = authHeader.substring(7);
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetails> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userDetails);
    }

}
