package com.example.bankapp.accounts.service;

import com.example.bankapp.accounts.model.EditUserRequestDto;
import com.example.bankapp.accounts.model.EmailNotification;
import com.example.bankapp.accounts.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSettingsService {
    private final UserService userService;
    private final NotificationsService notificationsService;

    public User editUser(User user, EditUserRequestDto dto) {
        userService.validateBirthdate(dto.getBirthdate(), user.getLogin());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            var msg = EmailNotification.builder()
                    .email(user.getEmail())
                    .subject("Обновление данных пользователя")
                    .message("Данные обновлены")
                    .build();
            notificationsService.addEmailNotification(msg);
        }
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setBirthdate(dto.getBirthdate());
        return userService.update(user);
    }

    @Transactional
    public User changePassword(User user, String newPassword) {
        if (newPassword.isBlank()) {
            log.warn("Пароль не может быть пустым, {}", user.getLogin());
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            var msg = EmailNotification.builder()
                    .email(user.getEmail())
                    .subject("Смена пароля")
                    .message("Пароль обновлен")
                    .build();
            notificationsService.addEmailNotification(msg);
        }
        user.setPassword(newPassword);
        return userService.update(user);
    }
}
