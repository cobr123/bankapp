package com.example.bankapp.transfer.service;

import com.example.bankapp.transfer.client.UserClient;
import com.example.bankapp.transfer.model.AccountChangeRequestDto;
import com.example.bankapp.transfer.model.Currency;
import com.example.bankapp.transfer.model.TransferRequestDto;
import com.example.bankapp.transfer.model.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final UserClient userClient;

    public Mono<Void> transfer(String fromLogin, TransferRequestDto dto) {
        if (fromLogin.equals(dto.getToLogin()) && dto.getFromCurrency().equals(dto.getToCurrency())) {
            return Mono.error(new IllegalArgumentException("Для перевода себе используйте снятие/пополнение наличных"));
        }
        if (!dto.getFromCurrency().equals(dto.getToCurrency())) {
            return Mono.error(new IllegalArgumentException("not implemented yet"));
        }
        if (dto.getValue().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Сумма должна быть больше нуля, {}", fromLogin);
            return Mono.error(new IllegalArgumentException("Сумма должна быть больше нуля"));
        }
        return Mono.zip(userClient.findByLogin(fromLogin), userClient.findByLogin(dto.getToLogin()))
                .flatMap(pair -> {
                    var fromUser = pair.getT1();
                    var toUser = pair.getT2();
                    var currency = dto.getFromCurrency();
                    return doTransfer(currency, dto.getValue(), fromUser, toUser);
                });
    }

    private Mono<Void> doTransfer(Currency currency, BigDecimal value, UserResponseDto fromUser, UserResponseDto toUser) {
        var fromAccount = fromUser.getAccounts().stream().filter(a -> a.getCurrency().equals(currency)).findFirst().orElseThrow();
        var toAccount = toUser.getAccounts().stream().filter(a -> a.getCurrency().equals(currency)).findFirst().orElseThrow();
        if (fromAccount.getValue().compareTo(value) < 0) {
            log.warn("Недостаточно средств для списания, {}", fromUser.getLogin());
            return Mono.error(new IllegalArgumentException("Недостаточно средств для списания"));
        }
        var changes = new ArrayList<AccountChangeRequestDto>();
        changes.add(AccountChangeRequestDto.builder()
                .login(fromUser.getLogin())
                .before(fromAccount.getValue())
                .after(fromAccount.getValue().add(value.negate()))
                .currency(fromAccount.getCurrency())
                .build());
        changes.add(AccountChangeRequestDto.builder()
                .login(toUser.getLogin())
                .before(toAccount.getValue())
                .after(toAccount.getValue().add(value))
                .currency(toAccount.getCurrency())
                .build());
        return userClient.transfer(changes);
    }
}
