package com.backend.Repository;

import com.backend.Entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    Optional<UserDevice> findByUserUserIdAndDeviceId(UUID userId, String deviceId);

    List<UserDevice> findAllByUserUserIdAndActiveTrue(UUID userId);

    List<UserDevice> findAllByUserUserId(UUID userId);

    boolean existsByUserUserIdAndDeviceId(UUID userId, String deviceId);

    @Modifying
    @Query("""
            UPDATE UserDevice d
            SET d.active = false
            WHERE d.user.userId = :userId
            AND d.deviceId = :deviceId
            """)
    void deactivateDevice(@Param("userId") UUID userId, @Param("deviceId") String deviceId);

    @Modifying
    @Query("""
            UPDATE UserDevice d
            SET d.active = false
            WHERE d.user.userId = :userId
            """)
    void deactivateAllDevices(@Param("userId") UUID userId);

    long countByUserUserIdAndActiveTrue(UUID userId);
}