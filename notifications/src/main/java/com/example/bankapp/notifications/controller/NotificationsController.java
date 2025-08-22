package com.example.bankapp.notifications.controller;

import com.example.bankapp.notifications.model.EmailNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class NotificationsController {

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody EmailNotificationDto dto) {
        log.warn("Sending email: {}", dto);
    }
}
