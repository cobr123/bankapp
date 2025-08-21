package com.example.bankapp.exchange.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public final class PostgreSqlTestContainer {

    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:17-alpine");

}