package com.study.redis.starter.core;

import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RedisAsyncService {

    private final RedisAsyncCommands<String, String> async;

    public RedisAsyncService(RedisAsyncCommands<String, String> async) {
        this.async = async;
    }

    public CompletableFuture<String> getAsync(String key) {
        return async.get(key).toCompletableFuture();
    }

    public CompletableFuture<String> setAsync(String key, String value) {
        return async.set(key, value).toCompletableFuture();
    }

    public CompletableFuture<Void> batchSetAsync(Map<String, String> kvMap) {
        return async.mset(kvMap).toCompletableFuture().thenAccept(r -> {});
    }

    public CompletableFuture<List<String>> getMultipleAsync(String... keys) {
        CompletableFuture<String>[] futures = new CompletableFuture[keys.length];
        for (int i = 0; i < keys.length; i++) {
            futures[i] = getAsync(keys[i]);
            
        }
        return CompletableFuture.allOf(futures)
                .thenApply(v -> {
                    List<String> results = new ArrayList<>();
                    for (CompletableFuture<String> f : futures) {
                        results.add(f.join());
                    }
                    return results;
                });
    }
}
