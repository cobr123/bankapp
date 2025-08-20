package com.example.bankapp.accounts.service;

import com.example.bankapp.accounts.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final UserService userService;
    private final AccountService accountService;

    @Transactional
    public void transfer(List<AccountChangeRequestDto> dto) {
        for (AccountChangeRequestDto request : dto) {
            if (request.getAfter().compareTo(BigDecimal.ZERO) < 0) {
                log.warn("Недостаточно средств для списания, {}", request.getLogin());
                throw new IllegalArgumentException("Недостаточно средств для списания");
            }
            var user = userService.findByLogin(request.getLogin()).orElseThrow();
            var account = accountService.findByUserIdAndCurrency(user.getId(), request.getCurrency())
                    .orElse(Account.builder()
                            .userId(user.getId())
                            .currency(request.getCurrency())
                            .value(BigDecimal.ZERO)
                            .build());
            if (account.getValue().compareTo(request.getBefore()) != 0) {
                throw new ConcurrentModificationException();
            }
            account.setValue(request.getAfter());
            accountService.save(account);
        }
    }

    @Transactional
    public void updateUserCurrency(Long userId, Set<Currency> currencies) {
        var currenciesToCreate = new HashSet<>(currencies);
        for (Account account : accountService.findByUserId(userId)) {
            currenciesToCreate.remove(account.getCurrency());
            if (!currencies.contains(account.getCurrency())) {
                if (account.getValue().compareTo(BigDecimal.ZERO) != 0) {
                    throw new IllegalArgumentException("Удалять можно только счета с нулевым балансом");
                }
                accountService.delete(account);
            }
        }
        for (Currency currency : currenciesToCreate) {
            var account = Account.builder()
                    .userId(userId)
                    .currency(currency)
                    .value(BigDecimal.ZERO)
                    .build();
            accountService.save(account);
        }
    }

    public void updateUserCash(Long userId, EditUserCashRequestDto dto) {
        if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Сумма должна быть больше нуля, {}", userId);
            throw new IllegalArgumentException("Сумма должна быть больше нуля");
        }
        var account = accountService.findByUserIdAndCurrency(userId, dto.getCurrency())
                .orElse(Account.builder().value(BigDecimal.ZERO).userId(userId).currency(dto.getCurrency()).build());
        if (EditUserCashAction.PUT.equals(dto.getAction())) {
            account.setValue(account.getValue().add(dto.getValue()));
            accountService.save(account);
        } else if (EditUserCashAction.GET.equals(dto.getAction())) {
            if (account.getValue().compareTo(dto.getValue()) < 0) {
                log.warn("Недостаточно средств для снятия, {}", userId);
                throw new IllegalArgumentException("Недостаточно средств для снятия");
            }
            account.setValue(account.getValue().add(dto.getValue().negate()));
            accountService.save(account);
        }
    }
}
