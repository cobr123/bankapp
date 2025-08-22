package com.example.bankapp.transfer.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blocker-client")
@RequiredArgsConstructor
@Getter
public class BlockerClientProperties {
    private final String baseUrl;
}
