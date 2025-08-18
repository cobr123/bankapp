package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.configuration.SecurityConfig;
import com.example.bankapp.ui.service.OAuth2Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

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

    @Test
    public void testMainNoAuth() {
        webTestClient.get().uri("/main").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    public void testMainWithAuth() {
        webTestClient.get().uri("/main").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html");
    }

}