package org.example.phishing_control_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.phishing_control_service.kafka.event.VerifiedUrlEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VerifiedUrlProducer {
    @Qualifier("verifiedUrlKafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.verified-url}")
    private String verifiedUrlTopic;


    public VerifiedUrlProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Async
    public void send(VerifiedUrlEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(verifiedUrlTopic, event.getId(), json)
                    .thenAccept(result -> log.info(" Sent VerifiedUrlEvent to topic '{}' at offset {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()))
                    .exceptionally(ex -> {
                        log.error("❌ Failed to send VerifiedUrlEvent", ex);
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error(" Failed to serialize VerifiedUrlEvent: {}", event, e);
            throw new RuntimeException("Serialization error", e);
        }
    }
}