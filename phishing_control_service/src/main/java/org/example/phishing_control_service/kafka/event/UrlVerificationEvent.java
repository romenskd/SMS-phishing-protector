package org.example.phishing_control_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UrlVerificationEvent {
    private String id;
    private String message;
    private Instant timestamp;
}
