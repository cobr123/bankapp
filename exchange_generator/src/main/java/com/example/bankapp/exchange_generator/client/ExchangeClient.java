package com.example.bankapp.exchange_generator.client;

import com.example.bankapp.exchange_generator.configuration.ExchangeClientProperties;
import com.example.bankapp.exchange_generator.model.UpdateRateRequestDto;
import com.example.bankapp.exchange_generator.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class ExchangeClient {

    private final RestClient restClient;
    private final OAuth2Service oAuth2Service;

    public ExchangeClient(RestClient.Builder builder, ExchangeClientProperties properties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.restClient = builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public void updateRates(List<UpdateRateRequestDto> dto) {
        try {
            restClient
                    .post()
                    .uri("/")
                    .header("Authorization", "Bearer " + oAuth2Service.getTokenValue())
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception error) {
            log.error("Error updateRates", error);
        }
    }

}
