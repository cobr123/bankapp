package com.example.bankapp.ui.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "transfer-client")
@RequiredArgsConstructor
@Getter
public class TransferClientProperties {
    private final String baseUrl;
}
