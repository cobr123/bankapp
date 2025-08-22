package com.example.bankapp.blocker.controller;

import com.example.bankapp.blocker.configuration.SecurityConfig;
import com.example.bankapp.blocker.model.AccountChangeRequestDto;
import com.example.bankapp.blocker.model.Currency;
import com.example.bankapp.blocker.model.EditUserCashAction;
import com.example.bankapp.blocker.model.EditUserCashRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.random.RandomGenerator;

import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest(BlockerController.class)
@Import({SecurityConfig.class})
@ActiveProfiles("test")
public class BlockerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;

    @MockitoBean
    private RandomGenerator randomGenerator;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testTransferNoAuth() {
        var dto = AccountChangeRequestDto.builder()
                .currency(Currency.RUB)
                .before(BigDecimal.ONE)
                .after(BigDecimal.ONE)
                .login("mary")
                .build();
        webTestClient
                .post()
                .uri("/transfer")
                .bodyValue(List.of(dto))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testTransferTrueWithAuth() {
        var dto = AccountChangeRequestDto.builder()
                .currency(Currency.RUB)
                .before(BigDecimal.ONE)
                .after(BigDecimal.ONE)
                .login("mary")
                .build();
        doReturn(true).when(randomGenerator).nextBoolean();
        webTestClient
                .mutateWith(mockUser("john"))
                .post()
                .uri("/transfer")
                .bodyValue(List.of(dto))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testTransferFalseWithAuth() {
        var dto = AccountChangeRequestDto.builder()
                .currency(Currency.RUB)
                .before(BigDecimal.ONE)
                .after(BigDecimal.ONE)
                .login("mary")
                .build();
        doReturn(false).when(randomGenerator).nextBoolean();
        webTestClient
                .mutateWith(mockUser("john"))
                .post()
                .uri("/transfer")
                .bodyValue(List.of(dto))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void testCashNoAuth() {
        var dto = EditUserCashRequestDto.builder()
                .login("john")
                .currency(Currency.RUB)
                .value(BigDecimal.ONE)
                .action(EditUserCashAction.PUT)
                .build();
        webTestClient
                .post()
                .uri("/cash")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testCashTrueWithAuth() {
        var dto = EditUserCashRequestDto.builder()
                .login("john")
                .currency(Currency.RUB)
                .value(BigDecimal.ONE)
                .action(EditUserCashAction.PUT)
                .build();
        doReturn(true).when(randomGenerator).nextBoolean();
        webTestClient
                .mutateWith(mockUser("john"))
                .post()
                .uri("/cash")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testCashFalseWithAuth() {
        var dto = EditUserCashRequestDto.builder()
                .login("john")
                .currency(Currency.RUB)
                .value(BigDecimal.ONE)
                .action(EditUserCashAction.PUT)
                .build();
        doReturn(false).when(randomGenerator).nextBoolean();
        webTestClient
                .mutateWith(mockUser("john"))
                .post()
                .uri("/cash")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(422);
    }
}
