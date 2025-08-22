package com.example.bankapp.cash.client;

import com.example.bankapp.cash.configuration.BlockerClientProperties;
import com.example.bankapp.cash.model.AccountChangeRequestDto;
import com.example.bankapp.cash.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class BlockerClient {

    private final RestClient restClient;
    private final OAuth2Service oAuth2Service;

    public BlockerClient(RestClient.Builder builder, BlockerClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.restClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public void checkTransfer(List<AccountChangeRequestDto> dto) {
        var accessToken = oAuth2Service.getTokenValue();
        restClient
                .post()
                .uri("/")
                .body(dto)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity();
    }

}
