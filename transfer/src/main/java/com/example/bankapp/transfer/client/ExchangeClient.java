package com.example.bankapp.transfer.client;

import com.example.bankapp.transfer.configuration.ExchangeClientProperties;
import com.example.bankapp.transfer.model.RateResponseDto;
import com.example.bankapp.transfer.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class ExchangeClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public ExchangeClient(WebClient.Builder builder, ExchangeClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Flux<RateResponseDto> getRates() {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flux()
                    .flatMap(accessToken -> {
                        return webClient
                                .get()
                                .uri("/rates")
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToFlux(RateResponseDto.class)
                                .onErrorResume(error -> {
                                    log.error("Error getRates", error);
                                    return Flux.empty();
                                });
                    });
        } catch (Exception error) {
            log.error("Error getRates", error);
            return Flux.empty();
        }
    }

}
