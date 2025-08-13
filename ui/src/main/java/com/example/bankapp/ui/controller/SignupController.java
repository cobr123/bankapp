package com.example.bankapp.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/signup")
public class SignupController {

    @GetMapping
    public Mono<String> getForm() {
        return Mono.just("signup");
    }

}