package org.example.user_service.service;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user_service.entity.SmsMessage;
import org.example.user_service.kafka.event.StartStopSmsEvent;
import org.example.user_service.kafka.event.VerifiedUrlEvent;
import org.example.user_service.repository.SmsMessageJpaRepository;
import org.example.user_service.repository.jdbc.SmsMessageJdbcRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsPersistenceService {
    private final SmsMessageJpaRepository jpaRepository;
    private final SmsMessageJdbcRepository jdbcRepository;
    private final RedisCacheService redisCacheService;
    private final Queue<SmsMessage> buffer = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean flushing = new AtomicBoolean(false);

    @Value("${sms-message.batch-size}")
    private int batchSize;

    public void saveStartStopEvent(StartStopSmsEvent event) {
        if (event == null || event.getId() == null) return;
        boolean isAllowed = isAllowedToVerify(event.getMessage());

        SmsMessage smsMessage = SmsMessage.builder()
                .id(event.getId())
                .isAllowed(isAllowed)
                .build();

        redisCacheService.saveIsAllowed(event.getId(), isAllowed);

        buffer.add(smsMessage);
        tryFlush();
    }

    public void saveVerifiedUrlEvent(VerifiedUrlEvent event) {
        if (event == null || event.getId() == null) return;

        SmsMessage smsMessage = SmsMessage.builder()
                .id(event.getId())
                .isSafe(event.isSafe())
                .build();

        buffer.add(smsMessage);
        tryFlush();
    }

    public Boolean isAllowedToCheckById(String id) {
        Boolean cachedIsAllowed = redisCacheService.getIsAllowed(id);
        if (cachedIsAllowed == null) {
            return jpaRepository.findById(id)
                    .map(SmsMessage::isAllowed)
                    .orElse(null);
        }
        return cachedIsAllowed;
    }

    private void tryFlush () {
        if (buffer.size() >= batchSize) {
            flushBatch();
        }
    }

    @Scheduled(fixedRate = 10000)
    public void flushBatch() {
        if (!flushing.compareAndSet(false, true)) {
            return;
        }

        try {
            if (buffer.isEmpty()) {
                return;
            }

            List<SmsMessage> batchToSave = new java.util.ArrayList<>(batchSize);
            for (int i = 0; i < batchSize; i++) {
                SmsMessage message = buffer.poll();
                if (message == null) break;
                batchToSave.add(message);
            }

            if (!batchToSave.isEmpty()) {
                jdbcRepository.saveAllBatch(batchToSave);
                log.info("Saved {} messages in batch", batchToSave.size());
            }

        } catch (Exception e) {
            log.error("Failed to save SMS batch", e);
        } finally {
            flushing.set(false);
        }
    }

    private boolean  isAllowedToVerify(String message) {
        return message.trim().equalsIgnoreCase("start");
    }

    @PreDestroy
    public void onShutdown() {
        flushBatch();
    }
}
