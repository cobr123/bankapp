package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.configuration.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(SignupController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class SignupControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetForm() {
        webTestClient
                .get().uri("/signup").exchange()
                .expectStatus().isOk();
    }

}
