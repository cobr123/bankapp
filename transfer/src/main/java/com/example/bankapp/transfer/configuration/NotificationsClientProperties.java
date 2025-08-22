package com.example.bankapp.transfer.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications-client")
@RequiredArgsConstructor
@Getter
public class NotificationsClientProperties {
    private final String baseUrl;
}
