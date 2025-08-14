package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.configuration.SecurityConfig;
import com.example.bankapp.ui.model.UserResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@WebFluxTest(SignupController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class SignupControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserClient userClient;

    @Test
    public void testGetForm() {
        webTestClient
                .get().uri("/signup").exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetPost() {
        doReturn(Mono.just(UserResponseDto.builder().build())).when(userClient).create(any());

        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/signup")
                        .queryParam("login", "login")
                        .queryParam("password", "password")
                        .queryParam("confirm_password", "password")
                        .queryParam("name", "name")
                        .queryParam("birthdate", LocalDate.now())
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetPostFailSamePassword() {
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/signup")
                        .queryParam("login", "login")
                        .queryParam("password", "password")
                        .queryParam("confirm_password", "confirm_password")
                        .queryParam("name", "name")
                        .queryParam("birthdate", LocalDate.now())
                        .build()
                )
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody()
                .xpath("//tr[2]/td").isEqualTo("Пароли не совпадают");
    }

}
