package com.example.bankapp.transfer.client;

import com.example.bankapp.transfer.configuration.BlockerClientProperties;
import com.example.bankapp.transfer.model.AccountChangeRequestDto;
import com.example.bankapp.transfer.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<List<AccountChangeRequestDto>> checkTransfer(List<AccountChangeRequestDto> dto) {
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
                                .toBodilessEntity()
                                .onErrorResume(WebClientResponseException.UnprocessableEntity.class, ex -> {
                                    return Mono.error(new IllegalArgumentException("Перевод заблокирован"));
                                })
                                .thenReturn(dto);
                    });
        } catch (Exception error) {
            log.error("Error checkTransfer {}", dto, error);
            return Mono.empty();
        }
    }

}
