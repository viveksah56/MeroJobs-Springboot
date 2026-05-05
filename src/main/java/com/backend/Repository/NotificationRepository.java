package com.backend.Repository;

import com.backend.Entity.Notification;
import com.backend.Enum.NotificationChannel;
import com.backend.Enum.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUser_UserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Notification> findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUser_UserIdAndReadFalse(UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.notificationId = :id")
    void markAsReadById(@Param("id") UUID id);

    Page<Notification> findByUser_UserIdAndTypeOrderByCreatedAtDesc(UUID userId, NotificationType type, Pageable pageable);

    Page<Notification> findByUser_UserIdAndChannelOrderByCreatedAtDesc(UUID userId, NotificationChannel channel, Pageable pageable);
}