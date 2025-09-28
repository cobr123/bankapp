package com.example.bankapp.notifications.consumer;

import com.example.bankapp.notifications.model.EmailNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailNotificationListener {

    @KafkaListener(topics = "email_notifications")
    public void listen(EmailNotificationDto dto, Acknowledgment acknowledgment) {
        log.warn("Sending email: {}", dto);
        acknowledgment.acknowledge();
    }
} 