package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.model.EditPasswordRequestDto;
import com.example.bankapp.ui.model.EditUserPasswordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            Model model,
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
                            .thenReturn("main");
                })
                .onErrorResume(err -> {
                    if (err.getClass() == IllegalArgumentException.class) {
                        model.addAttribute("passwordErrors", List.of(err.getMessage()));
                    } else {
                        model.addAttribute("passwordErrors", List.of("Ошибка смены пароля, попробуйте позже"));
                    }
                    return Mono.just("main");
                })
                .defaultIfEmpty("main");
    }
}
