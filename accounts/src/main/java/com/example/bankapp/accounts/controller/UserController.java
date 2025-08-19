package com.example.bankapp.accounts.controller;

import com.example.bankapp.accounts.model.*;
import com.example.bankapp.accounts.service.AccountService;
import com.example.bankapp.accounts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AccountService accountService;

    private final UserMapper userMapper;
    private final AccountMapper accountMapper;

    @PostMapping("/{login}/cash")
    public ResponseEntity<UserResponseDto> editUserCash(@PathVariable("login") String login, @RequestBody EditUserCashRequestDto dto) {
        return userService.findByLogin(login)
                .map(user -> {
                    updateUserCash(user.getId(), dto);
                    return user;
                })
                .map(user -> accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user)))
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    private void updateUserCash(Long userId, EditUserCashRequestDto dto) {
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

    @PostMapping("/{login}/editUserAccounts")
    public ResponseEntity<UserResponseDto> editUserAccounts(@PathVariable("login") String login, @RequestBody EditUserRequestDto dto) {
        userService.validateBirthdate(dto.getBirthdate(), login);
        return userService.findByLogin(login)
                .map(user -> {
                    updateUserCurrency(user.getId(), dto.getAccounts());
                    user.setName(dto.getName());
                    user.setEmail(dto.getEmail());
                    user.setDateOfBirth(dto.getBirthdate());
                    return userService.update(user);
                })
                .map(user -> accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user)))
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    private void updateUserCurrency(Long userId, Set<Currency> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            return;
        }
        var currenciesToCreate = new HashSet<>(currencies);
        for (Account account : accountService.findByUserId(userId)) {
            currenciesToCreate.remove(account.getCurrency());
            if (!currencies.contains(account.getCurrency())) {
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

    @PostMapping("/{login}/editPassword")
    public ResponseEntity<UserResponseDto> editPassword(@PathVariable("login") String login, @RequestBody EditPasswordRequestDto dto) {
        if (dto.getPassword().isBlank()) {
            log.warn("Пароль не может быть пустым, {}", login);
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        return userService.findByLogin(login)
                .map(user -> {
                    user.setPassword(dto.getPassword());
                    return userService.update(user);
                })
                .map(user -> accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user)))
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@RequestBody RegisterUserRequestDto dto) {
        var user = userService.createUser(dto);
        return accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user));
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserResponseDto> get(@PathVariable String login) {
        return userService.findByLogin(login)
                .map(user -> accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user)))
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

} 