package org.example.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/verification-allowed")
    public ResponseEntity<Boolean> isVerificationAllowed(@PathVariable String userId) {
        boolean isAllowed = userService.isVerificationAllowed(userId);
        return ResponseEntity.ok(isAllowed);
    }

}
