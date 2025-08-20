package com.example.bankapp.accounts.controller;

import com.example.bankapp.accounts.model.*;
import com.example.bankapp.accounts.service.AccountService;
import com.example.bankapp.accounts.service.TransferService;
import com.example.bankapp.accounts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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

    private final TransferService transferService;

    @PostMapping("/{login}/cash")
    public ResponseEntity<UserResponseDto> editUserCash(@PathVariable("login") String login, @RequestBody EditUserCashRequestDto dto) {
        return userService.findByLogin(login)
                .map(user -> {
                    transferService.updateUserCash(user.getId(), dto);
                    return user;
                })
                .map(user -> accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user)))
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/{login}/editUserAccounts")
    public ResponseEntity<UserResponseDto> editUserAccounts(@PathVariable("login") String login, @RequestBody EditUserRequestDto dto) {
        userService.validateBirthdate(dto.getBirthdate(), login);
        return userService.findByLogin(login)
                .map(user -> {
                    user.setName(dto.getName());
                    user.setEmail(dto.getEmail());
                    user.setBirthdate(dto.getBirthdate());
                    return userService.update(user);
                })
                .map(user -> {
                    transferService.updateUserCurrency(user.getId(), Optional.ofNullable(dto.getAccounts()).orElse(Set.of()));
                    return user;
                })
                .map(user -> accountMapper.toDto(accountService.findByUserId(user.getId()), userMapper.toDto(user)))
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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

    @GetMapping
    public List<UserLoginName> getAll() {
        return userService.findAllLoginName();
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody List<AccountChangeRequestDto> dto) {
        transferService.transfer(dto);
    }

} 