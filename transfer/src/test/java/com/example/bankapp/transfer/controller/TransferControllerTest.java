package com.example.bankapp.transfer.controller;

import com.example.bankapp.transfer.client.UserClient;
import com.example.bankapp.transfer.configuration.SecurityConfig;
import com.example.bankapp.transfer.model.AccountChangeRequestDto;
import com.example.bankapp.transfer.model.Currency;
import com.example.bankapp.transfer.model.TransferRequestDto;
import com.example.bankapp.transfer.service.TransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(TransferController.class)
@Import({SecurityConfig.class})
@ActiveProfiles("test")
public class TransferControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private TransferService transferService;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(transferService);
        Mockito.reset(userClient);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testTransferNoAuth() {
        var dto = TransferRequestDto.builder()
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .toLogin("mary")
                .build();
        webTestClient
                .post()
                .uri("/john")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testTransferWithAuth() {
        doReturn(Mono.just(List.of(AccountChangeRequestDto.builder().build()))).when(transferService).getChanges(anyString(), any());
        doReturn(Mono.empty()).when(userClient).transfer(any());

        var dto = TransferRequestDto.builder()
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .toLogin("mary")
                .build();
        webTestClient
                .mutateWith(mockUser("john"))
                .post()
                .uri("/john")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk();
    }
}
