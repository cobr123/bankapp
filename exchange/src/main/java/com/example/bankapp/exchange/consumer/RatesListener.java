package com.example.bankapp.exchange.consumer;

import com.example.bankapp.exchange.model.Rate;
import com.example.bankapp.exchange.model.UpdateRateRequestDto;
import com.example.bankapp.exchange.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class RatesListener {

    private final RateService rateService;

    @KafkaListener(topics = "rates")
    public void listen(UpdateRateRequestDto dto, Acknowledgment acknowledgment) {
        var rate = Rate.builder()
                .currency(dto.getCurrency())
                .value(dto.getValue())
                .build();
        rateService.update(rate);
        acknowledgment.acknowledge();
        log.info("Сообщение получено, {}", dto);
    }
} 