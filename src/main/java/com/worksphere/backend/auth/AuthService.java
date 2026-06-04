package com.worksphere.backend.auth;

import com.worksphere.backend.auth.dto.AuthResponse;
import com.worksphere.backend.auth.dto.LoginRequest;
import com.worksphere.backend.auth.dto.RegisterRequest;
import com.worksphere.backend.exception.EmailAlreadyExistsException;
import com.worksphere.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private AuthResponse buildAuthResponse(User user) {

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(
                        AuthResponse.UserInfo.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .build()
                )
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.MEMBER)
                .enabled(true)
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        return buildAuthResponse(user);
    }

}
