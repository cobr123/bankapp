package com.example.bankapp.ui.client;

import com.example.bankapp.ui.configuration.CashClientProperties;
import com.example.bankapp.ui.model.*;
import com.example.bankapp.ui.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CashClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public CashClient(WebClient.Builder builder, CashClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<UserResponseDto> editUserCash(EditUserCashRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/")
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(UserResponseDto.class)
                                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                                    return Mono.error(new IllegalArgumentException(ex.getResponseBodyAs(ErrorResponseDto.class).getDetail()));
                                });
                    });
        } catch (Exception error) {
            log.error("Error editUserCash user {}", dto, error);
            return Mono.empty();
        }
    }
}
