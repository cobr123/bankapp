package com.example.bankapp.exchange_generator.service;

import com.example.bankapp.exchange_generator.model.UpdateRateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ExchangeGeneratorServiceTest {

    @Autowired
    private ExchangeGeneratorService exchangeGeneratorService;

    @MockitoBean
    private KafkaTemplate<String, UpdateRateRequestDto> kafkaTemplate;

    @BeforeEach
    void setUp() {
        Mockito.reset(kafkaTemplate);
    }

    @Test
    public void testUpdateRates() throws ExecutionException, InterruptedException {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());

        exchangeGeneratorService.updateRates();
    }
}
