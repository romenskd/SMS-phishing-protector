package org.example.processing_message_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.processing_message_service.client.UserServiceFeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCheckPermissionService {
    private final UserServiceFeignClient userServiceClient;

    @Retryable(
            value = { RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 1.5)
    )
    public boolean checkPermission(String id) {
        try {
            return userServiceClient.isVerificationAllowed(id);
        } catch (Exception e) {
            log.error("Failed to check user verification permission");
            throw new RuntimeException("Failed to check user verification permission", e);
        }
    }
}
