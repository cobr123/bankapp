package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.configuration.SecurityConfig;
import com.example.bankapp.ui.model.AccountResponseDto;
import com.example.bankapp.ui.model.Currency;
import com.example.bankapp.ui.model.UserResponseDto;
import com.example.bankapp.ui.service.OAuth2Service;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(MainController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class MainControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private OAuth2Service oAuth2Service;
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userClient);
        Mockito.reset(oAuth2Service);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testMainNoAuth() {
        webTestClient.get().uri("/main").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    public void testMainWithAuth() {
        var accounts = List.of(
                AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.ZERO).build(),
                AccountResponseDto.builder().currency(Currency.USD).build()
        );
        doReturn(Mono.just(UserResponseDto.builder().accounts(accounts).build())).when(userClient).findByLogin(any());

        webTestClient.get().uri("/main").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html");
    }

}