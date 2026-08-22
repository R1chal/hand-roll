package com.study.redis.starter.health;

import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class RedisHealthIndicator implements HealthIndicator {

    private final RedisCommands<String, String> redis;

    public RedisHealthIndicator(RedisCommands<String, String> redis) {
        this.redis = redis;
    }

    @Override
    public Health health() {
        try {
            String pong = redis.ping();
            if ("PONG".equals(pong)) {
                return Health.up()
                        .withDetail("status", "Redis is reachable")
                        .build();
            } else {
                return Health.down()
                        .withDetail("status", "Unexpected response: " + pong)
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
