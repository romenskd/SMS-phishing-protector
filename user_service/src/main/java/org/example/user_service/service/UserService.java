package org.example.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final SmsPersistenceService smsPersistenceService;

    public boolean isVerificationAllowed(String userId) {
        return smsPersistenceService.isAllowedToCheckById(userId);
    }
}
