package org.example.user_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
@Table(name = "sms_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsMessage {

    @Id
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_allowed", nullable = false)
    private boolean isAllowed = false;

    @Column(name = "is_safe", nullable = false)
    private boolean isSafe = true;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}