package com.example.bankapp.ui.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private final MeterRegistry meterRegistry;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.fromCallable(() -> {
            meterRegistry.counter("login_failure",
                    "username", exception.getAuthenticationRequest().getName()
            ).increment();
            return null;
        }).then();
    }
}
