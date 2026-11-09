package com.banking.banking_app.controller;

import com.banking.banking_app.dto.request.LoginRequest;
import com.banking.banking_app.dto.request.RefreshTokenRequest;
import com.banking.banking_app.dto.request.RegisterRequest;
import com.banking.banking_app.dto.response.ApiResponse;
import com.banking.banking_app.dto.response.AuthResponse;
import com.banking.banking_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                authService.logout(userDetails.getUsername())
        );
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

} // ← } បិទ class នៅចុងបំផុត