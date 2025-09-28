package com.example.bankapp.exchange_generator.service;

import com.example.bankapp.exchange_generator.configuration.KafkaTopicConfig;
import com.example.bankapp.exchange_generator.model.Currency;
import com.example.bankapp.exchange_generator.model.UpdateRateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeGeneratorService {

    private final RandomGenerator random;
    private final KafkaTemplate<String, UpdateRateRequestDto> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void updateRates() throws ExecutionException, InterruptedException {
        var r1 = send(UpdateRateRequestDto.builder().currency(Currency.CNY).value(BigDecimal.valueOf(random.nextDouble(5, 15)).setScale(2, RoundingMode.HALF_UP)).build());
        var r2 = send(UpdateRateRequestDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(random.nextDouble(75, 85)).setScale(2, RoundingMode.HALF_UP)).build());

        r1.get();
        r2.get();
    }

    private CompletableFuture<SendResult<String, UpdateRateRequestDto>> send(UpdateRateRequestDto rate) {
        return kafkaTemplate.send(KafkaTopicConfig.RATES_TOPIC_NAME, rate.getCurrency().name(), rate)
                .whenComplete(this::logError);
    }

    private void logError(SendResult<String, UpdateRateRequestDto> result, Throwable e) {
        if (e != null) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
            return;
        }

        RecordMetadata metadata = result.getRecordMetadata();
        log.info("Сообщение отправлено. Topic = {}, partition = {}, offset = {}", metadata.topic(), metadata.partition(), metadata.offset());
    }
}
