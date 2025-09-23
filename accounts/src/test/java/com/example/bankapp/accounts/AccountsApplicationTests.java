package com.example.bankapp.accounts;

import com.example.bankapp.accounts.config.PostgreSqlTestContainer;
import com.example.bankapp.accounts.config.RedisTestContainer;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ImportTestcontainers({PostgreSqlTestContainer.class, RedisTestContainer.class})
@ActiveProfiles("test")
public abstract class AccountsApplicationTests {
}
