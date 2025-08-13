package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.configuration.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(RootController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class RootControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRoot() {
        webTestClient.get().uri("/").exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main");
    }

}