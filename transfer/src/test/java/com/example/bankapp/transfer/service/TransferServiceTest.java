package com.example.bankapp.transfer.service;

import com.example.bankapp.transfer.client.UserClient;
import com.example.bankapp.transfer.model.AccountResponseDto;
import com.example.bankapp.transfer.model.Currency;
import com.example.bankapp.transfer.model.TransferRequestDto;
import com.example.bankapp.transfer.model.UserResponseDto;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@ActiveProfiles("test")
public class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;
    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;
    @MockitoBean
    private ReactiveOAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userClient);
    }

    @Test
    public void testTransferNegativeValue() {
        var dto = TransferRequestDto.builder()
                .toLogin("mary")
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE.negate())
                .build();
        transferService.transfer("john", dto)
                .as(StepVerifier::create)
                .verifyErrorMessage("Сумма должна быть больше нуля");
    }

    @Test
    public void testTransferTwoCurrencies() {
        var dto = TransferRequestDto.builder()
                .toLogin("mary")
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.USD)
                .value(BigDecimal.ONE)
                .build();
        transferService.transfer("john", dto)
                .as(StepVerifier::create)
                .verifyErrorMessage("not implemented yet");
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
        transferService.transfer(login, dto)
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
        doReturn(Mono.empty()).when(userClient).transfer(any());

        var dto = TransferRequestDto.builder()
                .toLogin("mary")
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        transferService.transfer("john", dto)
                .as(StepVerifier::create)
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
        doReturn(Mono.empty()).when(userClient).transfer(any());

        var dto = TransferRequestDto.builder()
                .toLogin("mary")
                .fromCurrency(Currency.RUB)
                .toCurrency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        transferService.transfer("john", dto)
                .as(StepVerifier::create)
                .verifyErrorMessage("Недостаточно средств для списания");
    }
}
