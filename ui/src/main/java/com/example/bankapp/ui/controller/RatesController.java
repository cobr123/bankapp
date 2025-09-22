package com.example.bankapp.ui.controller;

import com.example.bankapp.ui.client.ExchangeClient;
import com.example.bankapp.ui.model.RateUiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rates")
public class RatesController {

    private final ExchangeClient exchangeClient;

    @GetMapping
    public Flux<RateUiResponseDto> getRates() {
        return exchangeClient.getAllForUi();
    }
}
