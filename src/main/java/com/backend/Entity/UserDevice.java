package com.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "user_devices",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "device_id"}),
        indexes = {
                @Index(columnList = "user_id"),
                @Index(columnList = "device_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userDeviceId;


    @Column(nullable = false, length = 36)
    private String deviceId;

    @Column(length = 20)
    private String deviceType;

    @Column(length = 50)
    private String os;

    @Column(length = 50)
    private String browser;

    @Column(length = 512)
    private String userAgent;

    @Column(length = 45)
    private String ipAddress;

    @Builder.Default
    @Column(nullable = false)
    private boolean trusted = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant firstSeenAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant lastSeenAt;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "userDevice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;
}