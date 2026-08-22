package com.study.redis.starter.core;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisService {

    private final RedisCommands<String, String> redis;

    public RedisService(RedisCommands<String, String> redis) {
        this.redis = redis;
    }

    public void set(String key, String value) {
        redis.set(key, value);
    }

    public String get(String key) {
        return redis.get(key);
    }

    public void setex(String key, long seconds, String value) {
        redis.setex(key, seconds, value);
    }

    public Long del(String... keys) {
        return redis.del(keys);
    }

    public boolean tryLock(String lockKey, String requestId, long expireSeconds) {
        String result = redis.set(lockKey, requestId, SetArgs.Builder.nx().ex(expireSeconds));
        return "OK".equals(result);
    }

    public boolean releaseLock(String lockKey, String requestId) {
        String script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """;
        Object result = redis.eval(script, ScriptOutputType.INTEGER, new String[]{lockKey}, requestId);
        return Long.valueOf(1).equals(result);
    }

    public Long incr(String key) {
        return redis.incr(key);
    }

    public void lpush(String key, String... values) {
        redis.lpush(key, values);
    }

    public String brpop(String key, long timeoutSeconds) {
        return redis.brpop(timeoutSeconds, key).getValue();
    }
}
