package com.banking.banking_app.service;

import com.banking.banking_app.dto.request.LoginRequest;
import com.banking.banking_app.dto.request.RefreshTokenRequest;
import com.banking.banking_app.dto.request.RegisterRequest;
import com.banking.banking_app.dto.response.ApiResponse;
import com.banking.banking_app.dto.response.AuthResponse;
import com.banking.banking_app.exception.CustomException;
import com.banking.banking_app.model.RefreshToken;
import com.banking.banking_app.model.Role;
import com.banking.banking_app.model.User;
import com.banking.banking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final RefreshTokenService refreshTokenService; // ✅ បន្ថែម

    // ====== REGISTER ======
    public ApiResponse<AuthResponse> register(RegisterRequest req) {

        // 1. ពិនិត្យ Email ស្ទួន
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(
                    "Email នេះមានរួចហើយ!",
                    HttpStatus.CONFLICT
            );
        }

        // 2. បង្កើត User
        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        // 3. Save ទៅ Database
        userRepository.save(user);

        // 4. ✅ Generate Access Token + Refresh Token
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        // 5. Return Response
        return ApiResponse.success("Register ជោគជ័យ!",
                buildAuthResponse(accessToken,
                        refreshToken.getToken(), user)
        );
    }

    // ====== LOGIN ======
    public ApiResponse<AuthResponse> login(LoginRequest req) {

        // 1. Authenticate
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );

        // 2. Load User
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new CustomException(
                        "User រកមិនឃើញ!",
                        HttpStatus.NOT_FOUND
                ));

        // 3. ✅ Generate Access Token + Refresh Token
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        // 4. Return Response
        return ApiResponse.success("Login ជោគជ័យ!",
                buildAuthResponse(accessToken,
                        refreshToken.getToken(), user)
        );
    }

    // ====== REFRESH TOKEN ======
    public ApiResponse<AuthResponse> refreshToken(
            RefreshTokenRequest req) {

        // 1. Verify Refresh Token
        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        req.getRefreshToken()
                );

        // 2. Generate Access Token ថ្មី
        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateToken(user);

        // 3. Return Response
        return ApiResponse.success("Refresh Token ជោគជ័យ!",
                buildAuthResponse(newAccessToken,
                        refreshToken.getToken(), user)
        );
    }

    // ====== LOGOUT ======
    public ApiResponse<?> logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        "User រកមិនឃើញ!",
                        HttpStatus.NOT_FOUND
                ));
        refreshTokenService.revokeByUser(user);
        return ApiResponse.success("Logout ជោគជ័យ!", null);
    }

    // ====== Helper Method ======
    private AuthResponse buildAuthResponse(
            String accessToken, String refreshToken, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}