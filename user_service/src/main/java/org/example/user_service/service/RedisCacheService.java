package org.example.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Boolean> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(20);

    public void saveIsAllowed(String id, boolean isAllowed) {
        redisTemplate.opsForValue().set(id, isAllowed, TTL);
    }

    public Boolean getIsAllowed(String id) {
        return redisTemplate.opsForValue().get(id);
    }
}