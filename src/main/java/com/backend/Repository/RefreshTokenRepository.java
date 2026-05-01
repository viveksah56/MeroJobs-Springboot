package com.backend.Repository;

import com.backend.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    boolean existsByToken(String token);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = true,
                r.revokedAt = CURRENT_TIMESTAMP
            WHERE r.userDevice.userDeviceId = :deviceId
            AND r.revoked = false
            """)
    void revokeAllByDeviceId(@Param("deviceId") UUID deviceId);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = true,
                r.revokedAt = CURRENT_TIMESTAMP
            WHERE r.userDevice.user.userId = :userId
            AND r.revoked = false
            """)
    void revokeAllByUserId(@Param("userId") UUID userId);

    @Query("""
            SELECT r FROM RefreshToken r
            WHERE r.userDevice.userDeviceId = :deviceId
            AND r.revoked = false
            AND r.expiresAt > CURRENT_TIMESTAMP
            """)
    Optional<RefreshToken> findActiveByDeviceId(@Param("deviceId") UUID deviceId);

    @Modifying
    @Query("""
            DELETE FROM RefreshToken r
            WHERE r.expiresAt < CURRENT_TIMESTAMP
            OR r.revoked = true
            """)
    void deleteExpiredAndRevoked();
}