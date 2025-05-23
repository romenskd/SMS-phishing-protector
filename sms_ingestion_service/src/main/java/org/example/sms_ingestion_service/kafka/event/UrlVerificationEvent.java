package org.example.sms_ingestion_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class UrlVerificationEvent {
    private String id;
    private String message;

    @Builder.Default
    Instant timestamp = Instant.now();
}
