package com.example.bankapp.blocker.controller;

import com.example.bankapp.blocker.model.AccountChangeRequestDto;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.random.RandomGenerator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class BlockerController {

    private final RandomGenerator random;
    private final MeterRegistry meterRegistry;

    @PostMapping("/")
    public ResponseEntity<Void> transfer(@RequestBody List<AccountChangeRequestDto> dto) {
        if (random.nextBoolean()) {
            return ResponseEntity.ok().build();
        } else {
            for (AccountChangeRequestDto accountChangeRequestDto : dto) {
                meterRegistry.counter("blocked_operation",
                        "username", accountChangeRequestDto.getLogin()
                ).increment();
            }
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

} 