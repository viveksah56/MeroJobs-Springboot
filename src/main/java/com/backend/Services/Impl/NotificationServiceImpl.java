package com.backend.Services.Impl;

import com.backend.Dto.Response.NotificationResponse;
import com.backend.Dto.Response.PaginationResponse;
import com.backend.Entity.Notification;
import com.backend.Entity.User;
import com.backend.Enum.NotificationChannel;
import com.backend.Event.NotificationEvent;
import com.backend.Mapper.NotificationMapper;
import com.backend.Repository.NotificationRepository;
import com.backend.Services.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @EventListener
    @Transactional
    public void send(NotificationEvent event) {
        Notification notification = save(event, NotificationChannel.WEBSOCKET);
        NotificationResponse response = notificationMapper.toResponse(notification);
        pushToWebSocket(event.getRecipientId(), response);
    }

    private void pushToWebSocket(UUID userId, NotificationResponse response) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                response
        );
    }

    @Override
    @Transactional
    public Notification save(NotificationEvent event, NotificationChannel channel) {
        Notification notification = Notification.builder()
                .type(event.getType())
                .channel(channel)
                .title(event.getTitle())
                .message(event.getMessage())
                .referenceId(event.getReferenceId())
                .referenceType(event.getReferenceType())
                .read(false)
                .sent(true)
                .build();

        notification.setUser(entityManager.getReference(User.class, event.getRecipientId()));

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<NotificationResponse> getAll(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notification> notificationPage = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable);
        return PaginationResponse.of(notificationPage.map(notificationMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResponse<NotificationResponse> getUnread(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notification> notificationPage = notificationRepository.findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable);
        return PaginationResponse.of(notificationPage.map(notificationMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUser_UserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.markAsReadById(notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}