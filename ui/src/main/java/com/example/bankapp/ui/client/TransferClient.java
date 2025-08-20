package com.example.bankapp.ui.client;

import com.example.bankapp.ui.configuration.TransferClientProperties;
import com.example.bankapp.ui.model.*;
import com.example.bankapp.ui.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TransferClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public TransferClient(WebClient.Builder builder, TransferClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<Void> transfer(String fromLogin, TransferRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/{login}", fromLogin)
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .toBodilessEntity()
                                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                                    return Mono.error(new IllegalArgumentException(ex.getResponseBodyAs(ErrorResponseDto.class).getDetail()));
                                })
                                .then();
                    });
        } catch (Exception error) {
            log.error("Error transfer {} from user {}", dto, fromLogin, error);
            return Mono.empty();
        }
    }

}
