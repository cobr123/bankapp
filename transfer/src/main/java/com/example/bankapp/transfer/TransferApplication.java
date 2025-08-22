package com.example.bankapp.transfer;

import com.example.bankapp.transfer.configuration.BlockerClientProperties;
import com.example.bankapp.transfer.configuration.ExchangeClientProperties;
import com.example.bankapp.transfer.configuration.UserClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableConfigurationProperties({UserClientProperties.class, ExchangeClientProperties.class, BlockerClientProperties.class})
public class TransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
