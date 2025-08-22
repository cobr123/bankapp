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
    private final NotificationsService notificationsService;

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
    public User updateUserCurrency(User user, Set<Currency> currencies) {
        var currenciesToCreate = new HashSet<>(currencies);
        for (Account account : accountService.findByUserId(user.getId())) {
            currenciesToCreate.remove(account.getCurrency());
            if (!currencies.contains(account.getCurrency())) {
                if (account.getValue().compareTo(BigDecimal.ZERO) != 0) {
                    throw new IllegalArgumentException("Удалять можно только счета с нулевым балансом");
                }
                accountService.delete(account);
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    var msg = EmailNotification.builder()
                            .email(user.getEmail())
                            .subject("Удаление счета")
                            .message("Счет " + account.getCurrency() + " удален")
                            .build();
                    notificationsService.addEmailNotification(msg);
                }
            }
        }
        for (Currency currency : currenciesToCreate) {
            var account = Account.builder()
                    .userId(user.getId())
                    .currency(currency)
                    .value(BigDecimal.ZERO)
                    .build();
            accountService.save(account);
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                var msg = EmailNotification.builder()
                        .email(user.getEmail())
                        .subject("Создание счета")
                        .message("Счет " + currency + " создан")
                        .build();
                notificationsService.addEmailNotification(msg);
            }
        }
        return user;
    }
}
