package com.example.bankapp.accounts.controller;

import com.example.bankapp.accounts.model.EditPasswordRequestDto;
import com.example.bankapp.accounts.model.RegisterUserRequestDto;
import com.example.bankapp.accounts.model.UserMapper;
import com.example.bankapp.accounts.model.UserResponseDto;
import com.example.bankapp.accounts.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

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
                .map(mapper::toDto)
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@RequestBody RegisterUserRequestDto dto) {
        return mapper.toDto(userService.createUser(dto));
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserResponseDto> get(@PathVariable String login) {
        return userService.findByLogin(login)
                .map(mapper::toDto)
                .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

} 