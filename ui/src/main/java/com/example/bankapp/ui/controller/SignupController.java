package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import com.example.bankapp.ui.model.RegisterUserRequestDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
@Slf4j
public class SignupController {

    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient;

    @GetMapping
    public Mono<String> getForm() {
        return Mono.just("signup");
    }

    @PostMapping
    public Mono<String> postForm(
            Model model,
            ServerWebExchange exchange,
            RegisterUserRequestDto form
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    if (securityContext.isPresent() && securityContext.get().getAuthentication().isAuthenticated()) {
                        throw new IllegalArgumentException("Вы уже зарегистрированы");
                    }
                    if (!form.getPassword().equals(form.getConfirmPassword())) {
                        log.warn("Пароли не совпадают, {}", form.getLogin());
                        throw new IllegalArgumentException("Пароли не совпадают");
                    }
                    return form.getLogin();
                })
                .flatMap(v -> {
                    UserDetails newUser = User.withUsername(form.getLogin()).password(form.getPassword()).passwordEncoder(passwordEncoder::encode).build();
                    var dto = RegisterUserRequestDto.builder()
                            .login(form.getLogin())
                            .password(newUser.getPassword())
                            .confirmPassword(newUser.getPassword())
                            .name(form.getName())
                            .birthdate(form.getBirthdate())
                            .build();
                    return userClient.create(dto);
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
                        model.addAttribute("errors", List.of(err.getMessage()));
                    } else {
                        model.addAttribute("errors", List.of("Ошибка регистрации, попробуйте позже"));
                    }
                    model.addAttribute("login", form.getLogin());
                    model.addAttribute("name", form.getName());
                    model.addAttribute("birthdate", form.getBirthdate());
                    return Mono.just("signup");
                })
                .defaultIfEmpty("signup");
    }
}