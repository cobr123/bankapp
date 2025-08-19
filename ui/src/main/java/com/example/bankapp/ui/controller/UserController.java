package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.model.EditPasswordRequestDto;
import com.example.bankapp.ui.model.EditUserCashRequestDto;
import com.example.bankapp.ui.model.form.EditUserCashDto;
import com.example.bankapp.ui.model.form.EditUserDto;
import com.example.bankapp.ui.model.form.EditUserPasswordDto;
import com.example.bankapp.ui.model.EditUserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient;

    @PostMapping("/{login}/editPassword")
    public Mono<String> postEditPassword(
            ServerWebExchange exchange,
            @PathVariable("login") String login,
            EditUserPasswordDto form
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    var loggedUser = securityContext.map(sc -> sc.getAuthentication().getName()).orElse("");
                    if (!login.equals(loggedUser)) {
                        log.warn("Логины не совпадают, {}", loggedUser);
                        throw new IllegalArgumentException("Логины не совпадают");
                    }
                    if (!form.getPassword().equals(form.getConfirmPassword())) {
                        log.warn("Пароли не совпадают, {}", loggedUser);
                        throw new IllegalArgumentException("Пароли не совпадают");
                    }
                    return loggedUser;
                })
                .flatMap(loggedUser -> {
                    UserDetails newUser = User.withUsername(loggedUser).password(form.getPassword()).passwordEncoder(passwordEncoder::encode).build();
                    var dto = EditPasswordRequestDto.builder()
                            .login(loggedUser)
                            .password(newUser.getPassword())
                            .build();
                    return userClient.editPassword(dto);
                })
                .flatMap(userUi -> {
                    return exchange.getSession()
                            .doOnNext(session -> {
                                SecurityContextImpl securityContext = new SecurityContextImpl();
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userUi.getLogin(), userUi.getPassword(), List.of());
                                securityContext.setAuthentication(authentication);

                                session.getAttributes().put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext);
                            })
                            .flatMap(WebSession::changeSessionId)
                            .thenReturn("redirect:/main");
                })
                .onErrorResume(err -> {
                    List<String> errors;
                    if (err.getClass() == IllegalArgumentException.class) {
                        errors = List.of(err.getMessage());
                    } else {
                        errors = List.of("Ошибка смены пароля, попробуйте позже");
                    }
                    return exchange.getSession()
                            .map(s -> {
                                s.getAttributes().put("passwordErrors", errors);
                                return "redirect:/main";
                            });
                })
                .defaultIfEmpty("redirect:/main");
    }

    @PostMapping("/{login}/editUserAccounts")
    public Mono<String> postEditUserAccounts(
            ServerWebExchange exchange,
            @PathVariable("login") String login,
            EditUserDto form
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    var loggedUser = securityContext.map(sc -> sc.getAuthentication().getName()).orElse("");
                    if (!login.equals(loggedUser)) {
                        log.warn("Логины не совпадают, {}", loggedUser);
                        throw new IllegalArgumentException("Логины не совпадают");
                    }
                    return loggedUser;
                })
                .flatMap(loggedUser -> {
                    var dto = EditUserRequestDto.builder()
                            .login(loggedUser)
                            .name(form.getName())
                            .birthdate(form.getBirthdate())
                            .email(form.getEmail())
                            .accounts(form.getAccount())
                            .build();
                    return userClient.editUserAccounts(dto);
                })
                .flatMap(userUi -> {
                    return exchange.getSession()
                            .doOnNext(session -> {
                                SecurityContextImpl securityContext = new SecurityContextImpl();
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userUi.getLogin(), userUi.getPassword(), List.of());
                                securityContext.setAuthentication(authentication);

                                session.getAttributes().put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext);
                            })
                            .flatMap(WebSession::changeSessionId)
                            .thenReturn("redirect:/main");
                })
                .onErrorResume(err -> {
                    List<String> errors;
                    if (err.getClass() == IllegalArgumentException.class) {
                        errors = List.of(err.getMessage());
                    } else {
                        errors = List.of("Ошибка смены данных пользователя, попробуйте позже");
                    }
                    return exchange.getSession()
                            .map(s -> {
                                s.getAttributes().put("userAccountsErrors", errors);
                                return "redirect:/main";
                            });
                })
                .defaultIfEmpty("redirect:/main");
    }

    @PostMapping("/{login}/cash")
    public Mono<String> postEditUserCash(
            ServerWebExchange exchange,
            @PathVariable("login") String login,
            EditUserCashDto form
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    var loggedUser = securityContext.map(sc -> sc.getAuthentication().getName()).orElse("");
                    if (!login.equals(loggedUser)) {
                        log.warn("Логины не совпадают, {}", loggedUser);
                        throw new IllegalArgumentException("Логины не совпадают");
                    }
                    return loggedUser;
                })
                .flatMap(loggedUser -> {
                    var dto = EditUserCashRequestDto.builder()
                            .login(loggedUser)
                            .currency(form.getCurrency())
                            .value(form.getValue())
                            .action(form.getAction())
                            .build();
                    return userClient.editUserCash(dto);
                })
                .flatMap(userUi -> {
                    return exchange.getSession()
                            .doOnNext(session -> {
                                SecurityContextImpl securityContext = new SecurityContextImpl();
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userUi.getLogin(), userUi.getPassword(), List.of());
                                securityContext.setAuthentication(authentication);

                                session.getAttributes().put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, securityContext);
                            })
                            .flatMap(WebSession::changeSessionId)
                            .thenReturn("redirect:/main");
                })
                .onErrorResume(err -> {
                    List<String> errors;
                    if (err.getClass() == IllegalArgumentException.class) {
                        errors = List.of(err.getMessage());
                    } else {
                        errors = List.of("Ошибка изменения баланса наличных, попробуйте позже");
                    }
                    return exchange.getSession()
                            .map(s -> {
                                s.getAttributes().put("cashErrors", errors);
                                return "redirect:/main";
                            });
                })
                .defaultIfEmpty("redirect:/main");
    }
}
