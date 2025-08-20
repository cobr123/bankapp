package com.example.bankapp.accounts.service;

import com.example.bankapp.accounts.model.User;
import com.example.bankapp.accounts.model.RegisterUserRequestDto;
import com.example.bankapp.accounts.model.UserLoginName;
import com.example.bankapp.accounts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public void validateBirthdate(LocalDate birthdate, String login) {
        if (!birthdate.isBefore(LocalDate.now().minusYears(18))) {
            log.warn("Вам должно быть больше 18 лет, {}", login);
            throw new IllegalArgumentException("Вам должно быть больше 18 лет");
        }
    }

    @Caching(
            evict = {@CacheEvict(value = "all_users_login_name", allEntries = true)},
            put = {@CachePut(value = "users", key = "#dto.getLogin()")}
    )
    public User createUser(RegisterUserRequestDto dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            log.warn("Пароль не может быть пустым, {}", dto.getLogin());
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        validateBirthdate(dto.getBirthdate(), dto.getLogin());
        if (userRepository.findByLogin(dto.getLogin()).isPresent()) {
            log.warn("Пользователь с таким именем уже существует, {}", dto.getLogin());
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        try {
            var user = com.example.bankapp.accounts.model.User.builder()
                    .login(dto.getLogin())
                    .password(dto.getPassword())
                    .name(dto.getName())
                    .birthdate(dto.getBirthdate())
                    .build();
            return userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("Ошибка регистрации, попробуйте позже");
        }
    }

    @Cacheable(value = "users", key = "#login", unless = "#result == null")
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Cacheable(value = "all_users_login_name")
    public List<UserLoginName> findAllLoginName() {
        return userRepository.findAllLoginName();
    }

    @Caching(
            evict = {@CacheEvict(value = "all_users_login_name", allEntries = true)},
            put = {@CachePut(value = "users", key = "#user.getLogin()")}
    )
    public User update(User user) {
        return userRepository.save(user);
    }

}