package com.example.bankapp.exchange.repository;

import com.example.bankapp.exchange.ExchangeApplicationTests;
import com.example.bankapp.exchange.model.Currency;
import com.example.bankapp.exchange.model.Rate;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RateRepositoryTest extends ExchangeApplicationTests {

    @Autowired
    private RateRepository rateRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    public void testInsert() {
        var rate = Rate.builder()
                .currency(Currency.RUB)
                .value(BigDecimal.ONE)
                .build();
        var savedRate = rateRepository.save(rate);
        assertTrue("Созданной записи должен был быть присвоен ID", savedRate.getId() != null);
    }

}