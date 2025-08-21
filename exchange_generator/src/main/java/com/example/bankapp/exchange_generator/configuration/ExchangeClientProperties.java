package com.example.bankapp.exchange_generator.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchange-client")
@RequiredArgsConstructor
@Getter
public class ExchangeClientProperties {
    private final String baseUrl;
}
