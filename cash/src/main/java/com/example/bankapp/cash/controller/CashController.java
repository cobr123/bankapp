package com.example.bankapp.cash.controller;

import com.example.bankapp.cash.client.UserClient;
import com.example.bankapp.cash.model.EditUserCashRequestDto;
import com.example.bankapp.cash.service.CashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class CashController {

    private final UserClient userClient;
    private final CashService cashService;

    @PostMapping
    public void editUserCash(@RequestBody EditUserCashRequestDto dto) {
        var user = userClient.findByLogin(dto.getLogin());
        cashService.updateUserCash(user, dto);
    }

} 