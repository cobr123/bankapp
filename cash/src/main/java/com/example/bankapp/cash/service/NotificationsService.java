package com.example.bankapp.cash.service;

import com.example.bankapp.cash.client.NotificationsClient;
import com.example.bankapp.cash.model.EmailNotification;
import com.example.bankapp.cash.model.EmailNotificationMapper;
import com.example.bankapp.cash.repository.EmailNotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsService {

    private final EmailNotificationsRepository emailNotificationsRepository;
    private final NotificationsClient notificationsClient;
    private final EmailNotificationMapper emailNotificationMapper;

    @Scheduled(fixedDelay = 1000)
    public void sendEmailNotifications() {
        for (EmailNotification emailNotification : emailNotificationsRepository.findAll(Pageable.ofSize(10))) {
            try {
                notificationsClient.notifyByEmail(emailNotificationMapper.toDto(emailNotification));
                emailNotificationsRepository.delete(emailNotification);
            } catch (Exception ex) {
                log.error("Не получилось отправить письмо {}", emailNotification.getId(), ex);
            }
        }
    }

    public EmailNotification addEmailNotification(EmailNotification emailNotification) {
        return emailNotificationsRepository.save(emailNotification);
    }
}
