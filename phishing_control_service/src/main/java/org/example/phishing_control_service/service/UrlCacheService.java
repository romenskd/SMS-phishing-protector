package org.example.phishing_control_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UrlCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(60);
    private static final String CACHE_PREFIX = "url:";

    public void put(String url, Boolean isSafe) {
        String key = CACHE_PREFIX + url;
        redisTemplate.opsForValue().set(key, isSafe, TTL);
    }

    public Boolean get(String url) {
        String key = CACHE_PREFIX + url;
        Boolean isSafe = (Boolean) redisTemplate.opsForValue().get(key);

        if (isSafe != null) {
            redisTemplate.expire(key, TTL);
        }

        return isSafe;
    }


}
