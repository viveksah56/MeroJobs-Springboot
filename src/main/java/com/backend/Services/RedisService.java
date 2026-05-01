package com.backend.Services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.ttl.blacklist}")
    private long blacklistTtl;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
        log.debug("Redis SET key '{}' with TTL {}s", key, ttlSeconds);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("Redis DELETE key '{}'", key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void blacklistAccessToken(String token, long ttlSeconds) {
        set(BLACKLIST_PREFIX + token, "blacklisted", ttlSeconds);
        log.info("Access token blacklisted with TTL {}s", ttlSeconds);
    }

    public boolean isTokenBlacklisted(String token) {
        return hasKey(BLACKLIST_PREFIX + token);
    }
}