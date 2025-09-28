package com.example.bankapp.exchange.service;

import com.example.bankapp.exchange.model.Rate;
import com.example.bankapp.exchange.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateService {

    private final RateRepository rateRepository;

    @Cacheable(value = "all_rates", key = "'all'")
    public List<Rate> findAll() {
        return rateRepository.findAll();
    }

    @CacheEvict(value = "all_rates", key = "'all'")
    @Transactional
    public void update(Rate rate) {
        rateRepository.deleteByCurrency(rate.getCurrency());
        rateRepository.save(rate);
    }
}
