package com.example.bankapp.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public Mono<String> getForm(Model model, ServerWebExchange exchange) {
        model.addAttribute("loginError", exchange.getRequest().getQueryParams().containsKey("error"));
        return Mono.just("login");
    }

}