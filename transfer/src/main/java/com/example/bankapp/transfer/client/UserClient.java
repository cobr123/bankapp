package com.example.bankapp.transfer.client;

import com.example.bankapp.transfer.configuration.UserClientProperties;
import com.example.bankapp.transfer.model.AccountChangeRequestDto;
import com.example.bankapp.transfer.model.UserResponseDto;
import com.example.bankapp.transfer.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class UserClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public UserClient(WebClient.Builder builder, UserClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<UserResponseDto> findByLogin(String login) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .get()
                                .uri("/{login}", login)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(UserResponseDto.class)
                                .onErrorResume(error -> {
                                    log.error("Error findByLogin user {}", login, error);
                                    return Mono.empty();
                                });
                    });
        } catch (WebClientResponseException.NotFound e) {
            log.warn("User {} not found", login);
            return Mono.empty();
        } catch (Exception error) {
            log.error("Error findByLogin user {}", login, error);
            return Mono.empty();
        }
    }

    public Mono<Void> transfer(List<AccountChangeRequestDto> dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/transfer")
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .toBodilessEntity()
                                .then();
                    });
        } catch (Exception error) {
            log.error("Error transfer {}", dto, error);
            return Mono.error(error);
        }
    }
}
