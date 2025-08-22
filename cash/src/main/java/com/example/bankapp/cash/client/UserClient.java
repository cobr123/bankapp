package com.example.bankapp.cash.client;

import com.example.bankapp.cash.configuration.UserClientProperties;
import com.example.bankapp.cash.model.AccountChangeRequestDto;
import com.example.bankapp.cash.model.UserResponseDto;
import com.example.bankapp.cash.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class UserClient {

    private final RestClient restClient;
    private final OAuth2Service oAuth2Service;

    public UserClient(RestClient.Builder builder, UserClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.restClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public UserResponseDto findByLogin(String login) {
        var accessToken = oAuth2Service.getTokenValue();
        return restClient
                .get()
                .uri("/{login}", login)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(UserResponseDto.class);
    }

    public void transfer(List<AccountChangeRequestDto> dto) {
        var accessToken = oAuth2Service.getTokenValue();
        restClient
                .post()
                .uri("/transfer")
                .body(dto)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }
}
