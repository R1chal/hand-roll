package com.study.redis.starter.autoconfigure;

import com.study.redis.starter.core.RedisAsyncService;
import com.study.redis.starter.core.RedisService;
import com.study.redis.starter.health.RedisHealthIndicator;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnClass(RedisClient.class)
@EnableConfigurationProperties(RedisProperties.class)
@ComponentScan(basePackages = "com.study.redis.starter")
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisClient redisClient(RedisProperties properties) {
        RedisURI.Builder uriBuilder = RedisURI.builder()
                .withHost(properties.getHost())
                .withPort(properties.getPort())
                .withTimeout(properties.getTimeout());

        if (properties.getPassword() != null && !properties.getPassword().isEmpty()) {
            if (properties.getUser() != null && !properties.getUser().isEmpty()
                    && !"default".equals(properties.getUser())) {
                uriBuilder.withAuthentication(properties.getUser(), properties.getPassword());
            } else {
                uriBuilder.withPassword(properties.getPassword());
            }
        }

        if (properties.isSsl()) {
            uriBuilder.withSsl(true);
        }

        RedisClient client = RedisClient.create(uriBuilder.build());
        client.setOptions(ClientOptions.builder()
                .autoReconnect(true)
                .pingBeforeActivateConnection(true)
                .build());

        return client;
    }

    @Bean
    @ConditionalOnMissingBean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCommands<String, String> redisCommands(
            StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisAsyncCommands<String, String> redisAsyncCommands(
            StatefulRedisConnection<String, String> connection) {
        return connection.async();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisService redisService(RedisCommands<String, String> redisCommands) {
        return new RedisService(redisCommands);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisAsyncService redisAsyncService(RedisAsyncCommands<String, String> asyncCommands) {
        return new RedisAsyncService(asyncCommands);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisHealthIndicator redisHealthIndicator(RedisCommands<String, String> redisCommands) {
        return new RedisHealthIndicator(redisCommands);
    }
}
