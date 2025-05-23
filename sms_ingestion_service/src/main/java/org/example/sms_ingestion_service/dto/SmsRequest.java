package org.example.sms_ingestion_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SmsRequest(
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
        String sender,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
        String recipient,

        @NotBlank
        @Size(max = 1600)
        String message
) {}