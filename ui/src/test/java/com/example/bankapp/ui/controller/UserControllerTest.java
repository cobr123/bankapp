package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.BlockerClient;
import com.example.bankapp.ui.client.TransferClient;
import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.configuration.SecurityConfig;
import com.example.bankapp.ui.service.OAuth2Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(UserController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private BlockerClient blockerClient;

    @MockitoBean
    private TransferClient transferClient;

    @MockitoBean
    private OAuth2Service oAuth2Service;
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userClient);
        Mockito.reset(blockerClient);
        Mockito.reset(transferClient);
        Mockito.reset(oAuth2Service);
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @WithMockUser("userLogin")
    public void testChangePassword() {
        doReturn(Mono.empty()).when(blockerClient).checkEditUserCash(any());
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/user/userLogin/editPassword")
                        .queryParam("password", "password")
                        .queryParam("confirm_password", "password")
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main");
    }

    @Test
    @WithMockUser("userLogin")
    public void testEditUserAccounts() {
        doReturn(Mono.empty()).when(blockerClient).checkEditUserCash(any());
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/user/userLogin/editUserAccounts")
                        .queryParam("name", "new name")
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main");
    }

    @Test
    @WithMockUser("userLogin1")
    public void testTransfer() {
        doReturn(Mono.empty()).when(blockerClient).checkEditUserCash(any());
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/user/userLogin1/transfer")
                        .queryParam("from_currency", "RUB")
                        .queryParam("to_currency", "RUB")
                        .queryParam("value", "1")
                        .queryParam("to_login", "userLogin2")
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main");
    }
}
