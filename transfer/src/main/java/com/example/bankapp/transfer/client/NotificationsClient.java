package com.example.bankapp.transfer.client;

import com.example.bankapp.transfer.configuration.NotificationsClientProperties;
import com.example.bankapp.transfer.model.EmailNotificationRequestDto;
import com.example.bankapp.transfer.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class NotificationsClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public NotificationsClient(WebClient.Builder builder, NotificationsClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<Void> notifyByEmail(EmailNotificationRequestDto dto) {
        try {
            return oAuth2Service
                    .getTokenValue()
                    .flatMap(accessToken -> {
                        return webClient
                                .post()
                                .uri("/email")
                                .bodyValue(dto)
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .toBodilessEntity()
                                .onErrorResume(error -> {
                                    log.error("Error notifyByEmail", error);
                                    return Mono.error(error);
                                })
                                .then();
                    });
        } catch (Exception error) {
            log.error("Error notifyByEmail", error);
            return Mono.error(error);
        }
    }

}
