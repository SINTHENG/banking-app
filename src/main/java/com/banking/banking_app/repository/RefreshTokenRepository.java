package com.banking.banking_app.repository;

import com.banking.banking_app.model.RefreshToken;
import com.banking.banking_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    // រក token ពេល Refresh
    Optional<RefreshToken> findByToken(String token);

    // រក token ដែលមិន revoked
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    // រក token តាម User
    Optional<RefreshToken> findByUser(User user);

    // លុប token ពេល Logout
    @Modifying
    int deleteByUser(User user);
}