package com.example.bankapp.ui.client;

import com.example.bankapp.ui.configuration.ExchangeClientProperties;
import com.example.bankapp.ui.model.RateUiResponseDto;
import com.example.bankapp.ui.service.OAuth2Service;
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

    public Flux<RateUiResponseDto> getAllForUi() {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flux()
                    .flatMap(accessToken -> {
                        return webClient
                                .get()
                                .uri("/")
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToFlux(RateUiResponseDto.class)
                                .onErrorResume(error -> {
                                    log.error("Error getAllForUi", error);
                                    return Flux.empty();
                                });
                    });
        } catch (Exception error) {
            log.error("Error getAllForUi", error);
            return Flux.empty();
        }
    }

}
