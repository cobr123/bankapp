package com.example.bankapp.exchange_generator.service;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(topics = {"rates"})
public class ExchangeGeneratorServiceTest {

    // отключаем @Scheduled в ExchangeGeneratorService.updateRates
    @MockitoBean
    private TaskScheduler taskScheduler;

    @Autowired
    private ExchangeGeneratorService exchangeGeneratorService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void testUpdateRates() throws ExecutionException, InterruptedException {
        // Явно объявляем консьюмера, который будем использовать для проверки фактических сообщений
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker),
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer()) {
            // 0. Подписываем консьюмера на топики
            consumerForTest.subscribe(List.of("rates"));

            // 1. Отправляем сообщение в топик
            exchangeGeneratorService.updateRates();

            // 2. Проверяем сообщение, которое мы только что отправили в rates
            var inputMessages = KafkaTestUtils.getRecords(consumerForTest);
            assertEquals(2, inputMessages.count());
        }
    }
}
