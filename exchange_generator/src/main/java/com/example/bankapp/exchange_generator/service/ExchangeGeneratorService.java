package com.example.bankapp.exchange_generator.service;

import com.example.bankapp.exchange_generator.client.ExchangeClient;
import com.example.bankapp.exchange_generator.model.Currency;
import com.example.bankapp.exchange_generator.model.UpdateRateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class ExchangeGeneratorService {

    private final ExchangeClient exchangeClient;

    @Scheduled(fixedDelay = 1000)
    public void updateRates() {
        var rnd = RandomGenerator.getDefault();
        var rates = List.of(
                UpdateRateRequestDto.builder().currency(Currency.CNY).value(BigDecimal.valueOf(rnd.nextDouble(5, 15)).setScale(2, RoundingMode.HALF_UP)).build(),
                UpdateRateRequestDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(rnd.nextDouble(75, 85)).setScale(2, RoundingMode.HALF_UP)).build()
        );
        exchangeClient.updateRates(rates);
    }

}
