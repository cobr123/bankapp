package com.example.bankapp.exchange_generator.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String RATES_TOPIC_NAME = "rates";

    @Bean
    public NewTopic ratesTopic() {
        return TopicBuilder.name(RATES_TOPIC_NAME)
                .partitions(2)
                .replicas(1)
                .build();
    }

}