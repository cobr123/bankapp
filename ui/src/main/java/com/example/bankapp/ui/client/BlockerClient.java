package com.example.bankapp.ui.client;

import com.example.bankapp.ui.configuration.BlockerClientProperties;
import com.example.bankapp.ui.model.EditUserCashRequestDto;
import com.example.bankapp.ui.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BlockerClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public BlockerClient(WebClient.Builder builder, BlockerClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<Void> checkEditUserCash(EditUserCashRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/cash")
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .toBodilessEntity()
                                .onErrorResume(WebClientResponseException.UnprocessableEntity.class, ex -> {
                                    return Mono.error(new IllegalArgumentException("Перевод заблокирован"));
                                })
                                .then();
                    });
        } catch (Exception error) {
            log.error("Error checkEditUserCash {} from user {}", dto, dto.getLogin(), error);
            return Mono.empty();
        }
    }

}
