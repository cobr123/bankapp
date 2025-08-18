package com.example.bankapp.ui.controller;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Controller
@RequestMapping("/main")
public class MainController {

    @GetMapping
    public Mono<String> mainPage(Model model) {
        return ReactiveSecurityContextHolder.getContext()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(securityContext -> {
                    return securityContext.map(sc -> sc.getAuthentication().getName()).orElseThrow(() -> new IllegalArgumentException("Login required"));
                })
                .flatMap(loggedUser -> {
                    model.addAttribute("login", loggedUser);
                    return Mono.just("main");
                })
                .onErrorResume(err -> {
                    return Mono.just("redirect:/login");
                })
                .defaultIfEmpty("main");
    }
}
