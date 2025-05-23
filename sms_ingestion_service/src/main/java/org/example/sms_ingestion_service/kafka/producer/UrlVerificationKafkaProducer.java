package org.example.sms_ingestion_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sms_ingestion_service.kafka.event.UrlVerificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlVerificationKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.url-verification}")
    private String urlVerificationTopic;

    @Async
    public void send(UrlVerificationEvent event) {

        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(urlVerificationTopic, event.getId(), json)
                    .thenAccept(result -> log.info("Sent to topic: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()))
                    .exceptionally(ex -> {
                        log.error("Failed to send event", ex);
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize UrlVerificationEvent: {}", event, e);
            throw new RuntimeException("Serialization error", e);
        }
    }
}
