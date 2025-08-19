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
            accountService.insert(account);
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