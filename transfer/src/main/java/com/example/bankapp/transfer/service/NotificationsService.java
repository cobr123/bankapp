package com.example.bankapp.transfer.service;

import com.example.bankapp.transfer.model.EmailNotification;
import com.example.bankapp.transfer.model.EmailNotificationMapper;
import com.example.bankapp.transfer.model.EmailNotificationRequestDto;
import com.example.bankapp.transfer.repository.EmailNotificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationsService {

    private final EmailNotificationsRepository emailNotificationsRepository;
    private final EmailNotificationMapper emailNotificationMapper;
    private final KafkaTemplate<String, EmailNotificationRequestDto> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void sendEmailNotifications() {
        for (EmailNotification emailNotification : emailNotificationsRepository.findAll(Pageable.ofSize(10))) {
            try {
                notifyByEmail(emailNotificationMapper.toDto(emailNotification)).get();
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

    private CompletableFuture<SendResult<String, EmailNotificationRequestDto>> notifyByEmail(EmailNotificationRequestDto emailNotification) {
        return kafkaTemplate.send("email_notifications", emailNotification.getEmail(), emailNotification)
                .whenComplete(this::logError);
    }

    private void logError(SendResult<String, EmailNotificationRequestDto> result, Throwable e) {
        if (e != null) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
            return;
        }

        RecordMetadata metadata = result.getRecordMetadata();
        log.info("Сообщение отправлено. Topic = {}, partition = {}, offset = {}", metadata.topic(), metadata.partition(), metadata.offset());
    }
}
