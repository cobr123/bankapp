package com.example.bankapp.notifications.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {


    public static final String EMAIL_NOTIFICATIONS_TOPIC_NAME = "email_notifications";

    @Bean
    public NewTopic emailNotificationsTopic() {
        return TopicBuilder.name(EMAIL_NOTIFICATIONS_TOPIC_NAME)
                .partitions(2)
                .replicas(1)
                .build();
    }

}