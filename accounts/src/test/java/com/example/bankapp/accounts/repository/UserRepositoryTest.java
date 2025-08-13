package com.example.bankapp.accounts.repository;

import com.example.bankapp.accounts.AccountsApplicationTests;
import com.example.bankapp.accounts.model.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(AccountsApplicationTests.class)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    public void testInsert() {
        var user = User.builder()
                .login("login")
                .email("email")
                .password("password")
                .dateOfBirth(LocalDate.now())
                .name("name")
                .balance(BigDecimal.ONE)
                .build();
        var savedUser = userRepository.save(user);
        assertTrue("Созданной записи должен был быть присвоен ID", savedUser.getId() != null);
    }

}