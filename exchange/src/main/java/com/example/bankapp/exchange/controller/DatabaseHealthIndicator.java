package com.example.bankapp.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up().withDetail("message", "Database is available").build();
            } else {
                return Health.down().withDetail("error", "Database is not responding").build();
            }
        } catch (SQLException e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}