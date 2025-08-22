package com.example.bankapp.ui.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cash-client")
@RequiredArgsConstructor
@Getter
public class CashClientProperties {
    private final String baseUrl;
}
