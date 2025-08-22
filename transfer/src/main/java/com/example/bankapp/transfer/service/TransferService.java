package com.example.bankapp.transfer.service;

import com.example.bankapp.transfer.client.ExchangeClient;
import com.example.bankapp.transfer.client.UserClient;
import com.example.bankapp.transfer.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final UserClient userClient;
    private final ExchangeClient exchangeClient;

    public Mono<List<AccountChangeRequestDto>> getChanges(String fromLogin, TransferRequestDto dto) {
        if (fromLogin.equals(dto.getToLogin()) && dto.getFromCurrency().equals(dto.getToCurrency())) {
            return Mono.error(new IllegalArgumentException("Для перевода себе используйте снятие/пополнение наличных"));
        }
        if (dto.getValue().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Сумма должна быть больше нуля, {}", fromLogin);
            return Mono.error(new IllegalArgumentException("Сумма должна быть больше нуля"));
        }
        if (dto.getFromCurrency().equals(dto.getToCurrency())) {
            return Mono.zip(userClient.findByLogin(fromLogin), userClient.findByLogin(dto.getToLogin()))
                    .map(pair -> {
                        var fromUser = pair.getT1();
                        var toUser = pair.getT2();
                        return getChanges(dto.getFromCurrency(), dto.getToCurrency(), dto.getValue(), dto.getValue(), fromUser, toUser);
                    });
        } else {
            return Mono.zip(userClient.findByLogin(fromLogin), userClient.findByLogin(dto.getToLogin()), exchangeClient.getRates().collectList())
                    .map(pair -> {
                        var fromUser = pair.getT1();
                        var toUser = pair.getT2();
                        var rates = pair.getT3();
                        var toValue = convert(dto, rates);
                        return getChanges(dto.getFromCurrency(), dto.getToCurrency(), dto.getValue(), toValue, fromUser, toUser);
                    });
        }
    }

    private BigDecimal convert(TransferRequestDto dto, List<RateResponseDto> rates) {
        var toValue = dto.getValue();
        if (!dto.getFromCurrency().equals(Currency.RUB)) {
            BigDecimal rate = rates.stream()
                    .filter(r -> r.getCurrency().equals(dto.getFromCurrency()))
                    .findFirst()
                    .map(RateResponseDto::getValue)
                    .orElseThrow();
            toValue = toValue
                    .multiply(rate);
        }
        if (!dto.getToCurrency().equals(Currency.RUB)) {
            BigDecimal rate = rates.stream()
                    .filter(r -> r.getCurrency().equals(dto.getToCurrency()))
                    .findFirst()
                    .map(RateResponseDto::getValue)
                    .orElseThrow();
            toValue = toValue
                    .divide(rate, MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP);
        }
        return toValue;
    }

    private List<AccountChangeRequestDto> getChanges(Currency fromCurrency, Currency toCurrency, BigDecimal fromValue, BigDecimal toValue, UserResponseDto fromUser, UserResponseDto toUser) {
        var fromAccount = fromUser.getAccounts().stream().filter(a -> a.getCurrency().equals(fromCurrency)).findFirst().orElseThrow();
        var toAccount = toUser.getAccounts().stream().filter(a -> a.getCurrency().equals(toCurrency)).findFirst().orElseThrow();
        if (fromAccount.getValue().compareTo(fromValue) < 0) {
            log.warn("Недостаточно средств для списания, {}", fromUser.getLogin());
            throw new IllegalArgumentException("Недостаточно средств для списания");
        }
        var changes = new ArrayList<AccountChangeRequestDto>();
        changes.add(AccountChangeRequestDto.builder()
                .login(fromUser.getLogin())
                .before(fromAccount.getValue())
                .after(fromAccount.getValue().add(fromValue.negate()))
                .currency(fromAccount.getCurrency())
                .build());
        changes.add(AccountChangeRequestDto.builder()
                .login(toUser.getLogin())
                .before(toAccount.getValue())
                .after(toAccount.getValue().add(toValue))
                .currency(toAccount.getCurrency())
                .build());
        return changes;
    }
}
