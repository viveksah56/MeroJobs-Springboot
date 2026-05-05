package com.backend.Services.Impl;

import com.backend.Event.NotificationEvent;
import com.backend.Services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(NotificationEvent event) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(event.getRecipientId().toString());
            message.setSubject(event.getTitle());
            message.setText(buildBody(event));

            // optional sender
            message.setFrom("no-reply@yourapp.com");

            mailSender.send(message);

            log.info("Email sent successfully to {}", event.getRecipientId());

        } catch (Exception e) {
            log.error("Email sending failed: {}", e.getMessage(), e);
        }
    }

    private String buildBody(NotificationEvent event) {
        return """
                %s
                
                %s
                
                Reference ID: %s
                Reference Type: %s
                
                Regards,
                Backend Team
                """.formatted(event.getTitle(), event.getMessage(), event.getReferenceId(), event.getReferenceType());
    }
}