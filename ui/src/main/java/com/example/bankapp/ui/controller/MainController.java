package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final UserClient userClient;

    @GetMapping
    public Mono<String> mainPage(ServerWebExchange exchange, Model model) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    return securityContext.map(sc -> sc.getAuthentication().getName()).orElseThrow(() -> new IllegalArgumentException("Login required"));
                })
                .flatMap(login -> exchange.getSession().zipWith(userClient.findByLogin(login)))
                .flatMap(pair -> {
                    var session = pair.getT1();
                    var userResponseDto = pair.getT2();

                    model.addAttribute("userAccountsErrors", session.getAttributes().remove("userAccountsErrors"));
                    model.addAttribute("passwordErrors", session.getAttributes().remove("passwordErrors"));

                    model.addAttribute("login", userResponseDto.getLogin());
                    model.addAttribute("name", userResponseDto.getName());
                    model.addAttribute("email", userResponseDto.getEmail());
                    model.addAttribute("birthdate", userResponseDto.getDateOfBirth());

                    model.addAttribute("accounts", userResponseDto.getAccounts());
                    return Mono.just("main");
                })
                .onErrorResume(err -> {
                    log.error(err.getMessage(), err);
                    return Mono.just("redirect:/");
                })
                .defaultIfEmpty("main");
    }
}
