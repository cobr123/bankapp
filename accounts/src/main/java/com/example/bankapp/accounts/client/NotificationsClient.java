package com.example.bankapp.accounts.client;

import com.example.bankapp.accounts.configuration.NotificationsClientProperties;
import com.example.bankapp.accounts.model.EmailNotificationRequestDto;
import com.example.bankapp.accounts.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class NotificationsClient {

    private final RestClient restClient;
    private final OAuth2Service oAuth2Service;

    public NotificationsClient(RestClient.Builder builder, NotificationsClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.restClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public void notifyByEmail(EmailNotificationRequestDto dto) {
        try {
            restClient
                    .post()
                    .uri("/email")
                    .header("Authorization", "Bearer " + oAuth2Service.getTokenValue())
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception error) {
            log.error("Error notifyByEmail", error);
        }
    }

}
