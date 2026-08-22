package com.study.demo.controller;

import com.study.redis.starter.core.RedisAsyncService;
import com.study.redis.starter.core.RedisService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/redis")
public class TestController {

    private final RedisService redisService;
    private final RedisAsyncService redisAsyncService;

    public TestController(RedisService redisService, RedisAsyncService redisAsyncService) {
        this.redisService = redisService;
        this.redisAsyncService = redisAsyncService;
    }

    @GetMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value) {
        redisService.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return redisService.get(key);
    }

    @GetMapping("/lock")
    public String lock(@RequestParam String lockKey) {
        String requestId = java.util.UUID.randomUUID().toString();
        boolean locked = redisService.tryLock(lockKey, requestId, 10);
        if (locked) {
            redisService.releaseLock(lockKey, requestId);
            return "Lock acquired and released";
        }
        return "Lock failed";
    }

    @GetMapping("/async/batch")
    public CompletableFuture<String> asyncBatch() {
        Map<String, String> data = new HashMap<>();
        data.put("async:a", "1");
        data.put("async:b", "2");
        data.put("async:c", "3");

        return redisAsyncService.batchSetAsync(data)
                .thenCompose(v -> redisAsyncService.getMultipleAsync("async:a", "async:b", "async:c"))
                .thenApply(results -> "Batch results: " + results);
    }

    @GetMapping("/health")
    public String health() {
        return "Check /actuator/health for detailed status";
    }
}
