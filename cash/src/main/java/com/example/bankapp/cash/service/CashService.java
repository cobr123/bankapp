package com.example.bankapp.cash.service;

import com.example.bankapp.cash.client.BlockerClient;
import com.example.bankapp.cash.client.UserClient;
import com.example.bankapp.cash.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashService {

    private final NotificationsService notificationsService;
    private final UserClient userClient;
    private final BlockerClient blockerClient;

    @Transactional
    public void updateUserCash(UserResponseDto user, EditUserCashRequestDto dto) {
        var changes = getChanges(user, dto);
        blockerClient.checkTransfer(changes);
        userClient.transfer(changes);
    }

    public List<AccountChangeRequestDto> getChanges(UserResponseDto user, EditUserCashRequestDto dto) {
        if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Сумма должна быть больше нуля, {}", user.getLogin());
            throw new IllegalArgumentException("Сумма должна быть больше нуля");
        }
        var account = user.getAccounts().stream().filter(u -> u.isExists() && u.getCurrency().equals(dto.getCurrency()))
                .findFirst()
                .orElse(AccountResponseDto.builder().value(BigDecimal.ZERO).currency(dto.getCurrency()).build());
        var changes = new ArrayList<AccountChangeRequestDto>();
        if (EditUserCashAction.PUT.equals(dto.getAction())) {
            changes.add(AccountChangeRequestDto.builder()
                    .login(user.getLogin())
                    .before(account.getValue())
                    .after(account.getValue().add(dto.getValue()))
                    .currency(account.getCurrency())
                    .build());

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                var msg = EmailNotification.builder()
                        .email(user.getEmail())
                        .subject("Внесение наличных")
                        .message("Внесение наличных " + dto.getCurrency() + " на сумму " + dto.getValue())
                        .build();
                notificationsService.addEmailNotification(msg);
            }
        } else if (EditUserCashAction.GET.equals(dto.getAction())) {
            if (account.getValue().compareTo(dto.getValue()) < 0) {
                log.warn("Недостаточно средств для снятия, {}", user.getLogin());
                throw new IllegalArgumentException("Недостаточно средств для снятия");
            }
            changes.add(AccountChangeRequestDto.builder()
                    .login(user.getLogin())
                    .before(account.getValue())
                    .after(account.getValue().add(dto.getValue().negate()))
                    .currency(account.getCurrency())
                    .build());

            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                var msg = EmailNotification.builder()
                        .email(user.getEmail())
                        .subject("Снятие наличных")
                        .message("Снятие наличных " + dto.getCurrency() + " на сумму " + dto.getValue())
                        .build();
                notificationsService.addEmailNotification(msg);
            }
        }
        return changes;
    }
}
