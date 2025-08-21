package com.example.bankapp.exchange_generator.service;

import com.example.bankapp.exchange_generator.client.ExchangeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
public class ExchangeGeneratorServiceTest {

    @Autowired
    private ExchangeGeneratorService exchangeGeneratorService;

    @MockitoBean
    private ExchangeClient exchangeClient;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(exchangeClient);
    }

    @Test
    public void testUpdateRates() {
        exchangeGeneratorService.updateRates();
    }
}
