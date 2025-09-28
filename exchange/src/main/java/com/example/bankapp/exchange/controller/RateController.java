package com.example.bankapp.exchange.controller;

import com.example.bankapp.exchange.model.RateResponseDto;
import com.example.bankapp.exchange.model.RateUiResponseDto;
import com.example.bankapp.exchange.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class RateController {

    private final RateService rateService;

    @GetMapping
    public List<RateUiResponseDto> getAllForUi() {
        return rateService.findAll().stream().map(r -> {
                    return RateUiResponseDto.builder()
                            .title(r.getCurrency().getTitle())
                            .name(r.getCurrency().name())
                            .value(r.getValue())
                            .build();
                })
                .toList();
    }

    @GetMapping("/rates")
    public List<RateResponseDto> getAll() {
        return rateService.findAll().stream().map(r -> {
                    return RateResponseDto.builder()
                            .currency(r.getCurrency())
                            .value(r.getValue())
                            .build();
                })
                .toList();
    }

}
