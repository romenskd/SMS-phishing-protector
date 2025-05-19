package org.example.phishing_control_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.OffsetDateTime;

@Entity
@Table(name="tracked_urls")
@Setter
@Getter
public class VerifiedUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String url;

    @Column(name = "is_safe")
    private Boolean safe;

    @Column(name = "last_accessed")
    @UpdateTimestamp
    private OffsetDateTime lastAccessed;

    @Column(name = "expiry_at")
    private OffsetDateTime expiryAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    private static final Duration URL_TTL = Duration.ofDays(30);

    @PreUpdate
    @PrePersist
    public void updateExpiry() {
        this.lastAccessed = OffsetDateTime.now();
        this.expiryAt = lastAccessed.plus(URL_TTL);
    }
}
