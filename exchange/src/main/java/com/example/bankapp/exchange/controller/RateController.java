package com.example.bankapp.exchange.controller;

import com.example.bankapp.exchange.model.Rate;
import com.example.bankapp.exchange.model.RateResponseDto;
import com.example.bankapp.exchange.model.RateUiResponseDto;
import com.example.bankapp.exchange.model.UpdateRateRequestDto;
import com.example.bankapp.exchange.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody List<UpdateRateRequestDto> dto) {
        var rates = dto.stream().map(r -> {
            return Rate.builder()
                    .currency(r.getCurrency())
                    .value(r.getValue())
                    .build();
        }).toList();
        rateService.updateAll(rates);
    }
}
