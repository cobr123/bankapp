package com.example.bankapp.transfer.service;

import com.example.bankapp.transfer.client.ExchangeClient;
import com.example.bankapp.transfer.client.UserClient;
import com.example.bankapp.transfer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doReturn;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private ExchangeClient exchangeClient;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userClient);
        Mockito.reset(exchangeClient);
    }

    @Test
    public void testTransferNegativeValue() {
        var dto = TransferRequestDto.builder()
                .toLogin("mary")
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE.negate())
                .build();
        transferService.getChanges("john", dto)
                .as(StepVerifier::create)
                .verifyErrorMessage("Сумма должна быть больше нуля");
    }

    @Test
    public void testTransferTwoCurrenciesFromRUB() {
        var mary = UserResponseDto.builder()
                .login("mary")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.USD).value(BigDecimal.ZERO).build()))
                .build();
        var john = UserResponseDto.builder()
                .login("john")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.valueOf(80)).build()))
                .build();
        doReturn(Mono.just(john)).when(userClient).findByLogin("john");
        doReturn(Mono.just(mary)).when(userClient).findByLogin("mary");
        doReturn(Flux.just(RateResponseDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(80)).build())).when(exchangeClient).getRates();

        var dto = TransferRequestDto.builder()
                .toLogin(mary.getLogin())
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.USD)
                .value(BigDecimal.valueOf(80))
                .build();
        transferService.getChanges("john", dto)
                .as(StepVerifier::create)
                .assertNext(changes -> {
                    assertThat(changes.size()).isEqualTo(2);

                    assertThat(changes.getFirst().getLogin()).isEqualTo(john.getLogin());
                    assertThat(changes.getFirst().getBefore()).isEqualTo(BigDecimal.valueOf(80));
                    assertThat(changes.getFirst().getAfter()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getFirst().getCurrency()).isEqualTo(Currency.RUB);

                    assertThat(changes.getLast().getLogin()).isEqualTo(mary.getLogin());
                    assertThat(changes.getLast().getBefore()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getLast().getAfter()).isEqualTo(BigDecimal.ONE);
                    assertThat(changes.getLast().getCurrency()).isEqualTo(Currency.USD);
                })
                .verifyComplete();
    }

    @Test
    public void testTransferTwoCurrenciesToRUB() {
        var mary = UserResponseDto.builder()
                .login("mary")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.ZERO).build()))
                .build();
        var john = UserResponseDto.builder()
                .login("john")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.USD).value(BigDecimal.ONE).build()))
                .build();
        doReturn(Mono.just(john)).when(userClient).findByLogin("john");
        doReturn(Mono.just(mary)).when(userClient).findByLogin("mary");
        doReturn(Flux.just(RateResponseDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(80)).build())).when(exchangeClient).getRates();

        var dto = TransferRequestDto.builder()
                .toLogin(mary.getLogin())
                .fromCurrency(Currency.USD)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        transferService.getChanges("john", dto)
                .as(StepVerifier::create)
                .assertNext(changes -> {
                    assertThat(changes.size()).isEqualTo(2);

                    assertThat(changes.getFirst().getLogin()).isEqualTo(john.getLogin());
                    assertThat(changes.getFirst().getBefore()).isEqualTo(BigDecimal.ONE);
                    assertThat(changes.getFirst().getAfter()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getFirst().getCurrency()).isEqualTo(Currency.USD);

                    assertThat(changes.getLast().getLogin()).isEqualTo(mary.getLogin());
                    assertThat(changes.getLast().getBefore()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getLast().getAfter()).isEqualTo(BigDecimal.valueOf(80));
                    assertThat(changes.getLast().getCurrency()).isEqualTo(Currency.RUB);
                })
                .verifyComplete();
    }

    @Test
    public void testTransferTwoCurrenciesViaRUB() {
        var mary = UserResponseDto.builder()
                .login("mary")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.CNY).value(BigDecimal.ZERO).build()))
                .build();
        var john = UserResponseDto.builder()
                .login("john")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.USD).value(BigDecimal.ONE).build()))
                .build();
        doReturn(Mono.just(john)).when(userClient).findByLogin("john");
        doReturn(Mono.just(mary)).when(userClient).findByLogin("mary");
        doReturn(Flux.just(
                RateResponseDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(80)).build(),
                RateResponseDto.builder().currency(Currency.CNY).value(BigDecimal.valueOf(10)).build()
        )).when(exchangeClient).getRates();

        var dto = TransferRequestDto.builder()
                .toLogin(mary.getLogin())
                .fromCurrency(Currency.USD)
                .toCurrency(Currency.CNY)
                .value(BigDecimal.ONE)
                .build();
        transferService.getChanges("john", dto)
                .as(StepVerifier::create)
                .assertNext(changes -> {
                    assertThat(changes.size()).isEqualTo(2);

                    assertThat(changes.getFirst().getLogin()).isEqualTo(john.getLogin());
                    assertThat(changes.getFirst().getBefore()).isEqualTo(BigDecimal.ONE);
                    assertThat(changes.getFirst().getAfter()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getFirst().getCurrency()).isEqualTo(Currency.USD);

                    assertThat(changes.getLast().getLogin()).isEqualTo(mary.getLogin());
                    assertThat(changes.getLast().getBefore()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getLast().getAfter()).isEqualTo(BigDecimal.valueOf(8));
                    assertThat(changes.getLast().getCurrency()).isEqualTo(Currency.CNY);
                })
                .verifyComplete();
    }

    @Test
    public void testTransferSameUserSameCurrencies() {
        var login = "john";
        var dto = TransferRequestDto.builder()
                .toLogin(login)
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        transferService.getChanges(login, dto)
                .as(StepVerifier::create)
                .verifyErrorMessage("Для перевода себе используйте снятие/пополнение наличных");
    }

    @Test
    public void testTransferTwoUsers() {
        var mary = UserResponseDto.builder()
                .login("mary")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.ZERO).build()))
                .build();
        var john = UserResponseDto.builder()
                .login("john")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.ONE).build()))
                .build();
        doReturn(Mono.just(john)).when(userClient).findByLogin("john");
        doReturn(Mono.just(mary)).when(userClient).findByLogin("mary");

        var dto = TransferRequestDto.builder()
                .toLogin(mary.getLogin())
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        transferService.getChanges("john", dto)
                .as(StepVerifier::create)
                .assertNext(changes -> {
                    assertThat(changes.size()).isEqualTo(2);

                    assertThat(changes.getFirst().getLogin()).isEqualTo(john.getLogin());
                    assertThat(changes.getFirst().getBefore()).isEqualTo(BigDecimal.ONE);
                    assertThat(changes.getFirst().getAfter()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getFirst().getCurrency()).isEqualTo(Currency.RUB);

                    assertThat(changes.getLast().getLogin()).isEqualTo(mary.getLogin());
                    assertThat(changes.getLast().getBefore()).isEqualTo(BigDecimal.ZERO);
                    assertThat(changes.getLast().getAfter()).isEqualTo(BigDecimal.ONE);
                    assertThat(changes.getLast().getCurrency()).isEqualTo(Currency.RUB);
                })
                .verifyComplete();
    }

    @Test
    public void testTransferInsufficientFunds() {
        var mary = UserResponseDto.builder()
                .login("mary")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.ZERO).build()))
                .build();
        var john = UserResponseDto.builder()
                .login("john")
                .accounts(List.of(AccountResponseDto.builder().currency(Currency.RUB).value(BigDecimal.ZERO).build()))
                .build();
        doReturn(Mono.just(john)).when(userClient).findByLogin("john");
        doReturn(Mono.just(mary)).when(userClient).findByLogin("mary");
        doReturn(Flux.empty()).when(exchangeClient).getRates();

        var dto = TransferRequestDto.builder()
                .toLogin(mary.getLogin())
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        transferService.getChanges("john", dto)
                .as(StepVerifier::create)
                .verifyErrorMessage("Недостаточно средств для списания");
    }
}
