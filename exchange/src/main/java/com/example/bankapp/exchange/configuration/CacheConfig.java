package com.example.bankapp.exchange.configuration;

import com.example.bankapp.exchange.model.Rate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheCustomizer() {
        return builder -> builder
                .withCacheConfiguration(
                        "all_rates",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(
                                                new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Rate.class)
                                        )
                                ))
                );
    }
}
