package org.example.user_service.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class StartStopSmsEvent {
    private String id;
    private String message;

    @Builder.Default
    Instant timestamp = Instant.now();
}
