package com.example.bankapp.accounts.service;

import com.example.bankapp.accounts.AccountsApplicationTests;
import com.example.bankapp.accounts.model.RegisterUserRequestDto;
import com.example.bankapp.accounts.model.User;
import com.example.bankapp.accounts.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Import(AccountsApplicationTests.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        Mockito.reset(userRepository);
    }

    @Test
    public void testCreateUser() {
        var dto = RegisterUserRequestDto.builder()
                .login("john")
                .password("123")
                .confirmPassword("123")
                .name("Doe John")
                .birthdate(LocalDate.now().minusYears(19))
                .build();
        var user = User.builder().build();
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        userService.createUser(dto);
    }

    @Test
    public void testCreateUserErrorConfirmPass() {
        var dto = RegisterUserRequestDto.builder()
                .login("john")
                .password("1")
                .confirmPassword("2")
                .name("Doe John")
                .birthdate(LocalDate.now().minusYears(19))
                .build();
        var user = User.builder().build();
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    userService.createUser(dto);
                }
        );
    }

    @Test
    public void testCreateUserErrorTooYoung() {
        var dto = RegisterUserRequestDto.builder()
                .login("john")
                .password("123")
                .confirmPassword("123")
                .name("Doe John")
                .birthdate(LocalDate.now().minusYears(17))
                .build();
        var user = User.builder().build();
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    userService.createUser(dto);
                }
        );
    }
}
