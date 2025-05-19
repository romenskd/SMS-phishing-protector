package org.example.sms_ingestion_service.dto;

import java.time.Instant;

public record SmsResponse(
        String status,
        String message,
        Instant timestamp
) {
    public static SmsResponse success(String message) {
        return new SmsResponse("SUCCESS", message, Instant.now());
    }
}