package com.example.bankapp.accounts.configuration;

import com.example.bankapp.accounts.model.Account;
import com.example.bankapp.accounts.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return builder -> builder
                .withCacheConfiguration(
                        "users",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(objectMapper, User.class)
                                ))
                )
                .withCacheConfiguration(
                        "accounts",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(Account.class)
                                ))
                )
                .withCacheConfiguration(
                        "user_accounts",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(
                                                new ObjectMapper().getTypeFactory().constructCollectionType(List.class, Account.class)
                                        )
                                ))
                );
    }
}
