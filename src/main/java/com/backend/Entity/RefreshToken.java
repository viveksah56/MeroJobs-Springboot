package com.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_token", columnList = "token"),
                @Index(name = "idx_user_device", columnList = "user_device_id"),
                @Index(name = "idx_expires_at", columnList = "expires_at"),
                @Index(name = "idx_device_revoked", columnList = "user_device_id, revoked")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID refreshTokenId;


    @Column(nullable = false, unique = true, length = 512)
    private String token;


    @Column(nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    private Instant revokedAt;

    @Column(columnDefinition = "TEXT")
    private String replacedByToken;

    private Instant lastUsedAt;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return !revoked
                && !isExpired()
                && userDevice != null
                && userDevice.isActive();
    }

    public void revoke(String replacedByTokenHash) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.replacedByToken = replacedByTokenHash;
    }

    public void markUsed() {
        this.lastUsedAt = Instant.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_device_id", nullable = false)
    private UserDevice userDevice;
}