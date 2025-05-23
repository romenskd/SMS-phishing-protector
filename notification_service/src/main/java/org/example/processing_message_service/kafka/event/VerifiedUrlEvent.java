package org.example.processing_message_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VerifiedUrlEvent {
    private String id;
    private String message;
    private boolean isSafe;
}
