package com.example.bankapp.ui.client;

import com.example.bankapp.ui.configuration.UserClientProperties;
import com.example.bankapp.ui.model.*;
import com.example.bankapp.ui.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

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

    public Mono<UserResponseDto> create(RegisterUserRequestDto dto) {
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
            log.error("Error create user {}", dto, error);
            return Mono.empty();
        }
    }

    public Mono<UserResponseDto> editPassword(EditPasswordRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/{login}/editPassword", dto.getLogin())
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(UserResponseDto.class)
                                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                                    return Mono.error(new IllegalArgumentException(ex.getResponseBodyAs(ErrorResponseDto.class).getDetail()));
                                });
                    });
        } catch (Exception error) {
            log.error("Error editPassword user {}", dto, error);
            return Mono.empty();
        }
    }

    public Mono<UserResponseDto> editUserAccounts(EditUserRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/{login}/editUserAccounts", dto.getLogin())
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(UserResponseDto.class)
                                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                                    return Mono.error(new IllegalArgumentException(ex.getResponseBodyAs(ErrorResponseDto.class).getDetail()));
                                });
                    });
        } catch (Exception error) {
            log.error("Error editUserAccounts user {}", dto, error);
            return Mono.empty();
        }
    }

    public Mono<UserResponseDto> editUserCash(EditUserCashRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/{login}/cash", dto.getLogin())
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(UserResponseDto.class)
                                .onErrorResume(WebClientResponseException.BadRequest.class, ex -> {
                                    return Mono.error(new IllegalArgumentException(ex.getResponseBodyAs(ErrorResponseDto.class).getDetail()));
                                });
                    });
        } catch (Exception error) {
            log.error("Error editUserAccounts user {}", dto, error);
            return Mono.empty();
        }
    }
}
