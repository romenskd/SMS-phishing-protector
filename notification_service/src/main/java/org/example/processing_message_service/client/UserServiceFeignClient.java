package org.example.processing_message_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/users/{userId}/verification-allowed")
    boolean isVerificationAllowed(@PathVariable("userId") String userId);
}
