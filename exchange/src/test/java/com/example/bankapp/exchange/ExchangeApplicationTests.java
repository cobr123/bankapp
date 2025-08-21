package com.example.bankapp.exchange;

import com.example.bankapp.exchange.config.PostgreSqlTestContainer;
import com.example.bankapp.exchange.config.RedisTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ImportTestcontainers({PostgreSqlTestContainer.class, RedisTestContainer.class})
@ActiveProfiles("test")
public class ExchangeApplicationTests {
}
