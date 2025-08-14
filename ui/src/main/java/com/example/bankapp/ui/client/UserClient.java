package com.example.bankapp.ui.client;

import com.example.bankapp.ui.configuration.UserClientProperties;
import com.example.bankapp.ui.model.RegisterUserRequestDto;
import com.example.bankapp.ui.model.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder builder, UserClientProperties properties) {
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<UserResponseDto> findByLogin(String login) {
        try {
            return webClient
                    .get()
                    .uri("/{login}", login)
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .onErrorResume(error -> {
                        log.error("Error findByLogin user {}", login, error);
                        return Mono.empty();
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
            return webClient
                    .post()
                    .uri("/")
                    .bodyValue(dto)
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .onErrorResume(error -> {
                        log.error("Error create user {}", dto, error);
                        return Mono.empty();
                    });
        } catch (Exception error) {
            log.error("Error create user {}", dto, error);
            return Mono.empty();
        }
    }
}
