package com.example.bankapp.transfer.controller;

import com.example.bankapp.transfer.client.UserClient;
import com.example.bankapp.transfer.model.TransferRequestDto;
import com.example.bankapp.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class TransferController {

    private final TransferService transferService;
    private final UserClient userClient;

    @PostMapping("/{login}")
    public Mono<Void> transfer(@PathVariable("login") String fromLogin, @RequestBody TransferRequestDto dto) {
        return transferService.getChanges(fromLogin, dto)
                .flatMap(userClient::transfer);
    }

} 