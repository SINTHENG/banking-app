package com.banking.banking_app.service;
import com.banking.banking_app.model.RefreshToken;
import com.banking.banking_app.model.User;
import com.banking.banking_app.exception.CustomException;
import com.banking.banking_app.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // ✅ dash មិនមែន underscore!
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // Delete token ចាស់
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        // Generate secure random token
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Build & Save
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshExpiration)
        );
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() -> new CustomException(
                                "Refresh Token មិនត្រឹមត្រូវ!",
                                HttpStatus.UNAUTHORIZED
                        ));

        if (refreshToken.isRevoked()) {
            throw new CustomException(
                    "Refresh Token ត្រូវបាន revoke!",
                    HttpStatus.UNAUTHORIZED
            );
        }

        if (!refreshToken.getExpiryDate().isAfter(Instant.now())) {
            throw new CustomException(
                    "Refresh Token អស់សុពលភាព! Login ម្តងទៀត",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revokeByUser(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }
}