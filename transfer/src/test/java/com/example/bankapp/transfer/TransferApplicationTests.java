package com.example.bankapp.transfer;

import com.example.bankapp.transfer.config.PostgreSqlTestContainer;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ImportTestcontainers(PostgreSqlTestContainer.class)
@ActiveProfiles("test")
public abstract class TransferApplicationTests {
}
