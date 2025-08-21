package com.example.bankapp.exchange.service;

import com.example.bankapp.exchange.ExchangeApplicationTests;
import com.example.bankapp.exchange.model.Currency;
import com.example.bankapp.exchange.model.Rate;
import com.example.bankapp.exchange.repository.RateRepository;
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

import java.math.BigDecimal;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ExchangeApplicationTests.class)
@ActiveProfiles("test")
public class RateServiceTest {

    @Autowired
    private RateService rateService;

    @MockitoBean
    private RateRepository rateRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        Mockito.reset(rateRepository);
    }

    @Test
    public void testUpdateAll() {
        var rate = Rate.builder()
                .currency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        rateService.updateAll(List.of(rate));
    }
}
