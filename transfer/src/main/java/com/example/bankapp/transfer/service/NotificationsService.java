package com.example.bankapp.transfer.service;

import com.example.bankapp.transfer.client.NotificationsClient;
import com.example.bankapp.transfer.model.EmailNotification;
import com.example.bankapp.transfer.model.EmailNotificationMapper;
import com.example.bankapp.transfer.repository.EmailNotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
                notificationsClient.notifyByEmail(emailNotificationMapper.toDto(emailNotification)).block();
                emailNotificationsRepository.delete(emailNotification);
            } catch (Exception ex) {
                log.error("Не получилось отправить письмо {}", emailNotification.getId(), ex);
            }
        }
    }

    public Mono<EmailNotification> addEmailNotification(EmailNotification emailNotification) {
        if (emailNotification.getEmail() != null && !emailNotification.getEmail().isBlank()) {
            return Mono.fromCallable(() -> emailNotificationsRepository.save(emailNotification))
                    .subscribeOn(Schedulers.boundedElastic());
        } else {
            return Mono.empty();
        }
    }
}
