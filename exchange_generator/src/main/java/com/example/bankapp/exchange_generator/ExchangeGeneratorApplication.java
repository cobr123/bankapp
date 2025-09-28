package com.example.bankapp.exchange_generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.random.RandomGenerator;

@SpringBootApplication
@EnableScheduling
public class ExchangeGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeGeneratorApplication.class, args);
    }

    @Bean
    public RandomGenerator randomGenerator(){
        return RandomGenerator.getDefault();
    }
}
