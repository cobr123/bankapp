package com.example.bankapp.transfer;

import com.example.bankapp.transfer.config.PostgreSqlTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSqlTestContainer.class)
@ActiveProfiles("test")
public class TransferApplicationTests {
}
