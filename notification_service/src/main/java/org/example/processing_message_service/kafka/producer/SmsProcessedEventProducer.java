package org.example.processing_message_service.kafka.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.processing_message_service.kafka.event.PlainTextSmsEvent;
import org.example.processing_message_service.kafka.event.VerifiedUrlEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsProcessedEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.sms-processed}")
    private String smsProcessedTopic;

    @Async
    public void sendPlainText(PlainTextSmsEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(smsProcessedTopic, event.getId(), json)
                    .thenAccept(result -> log.info("Sent PlainTextSmsEvent to topic: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()))
                    .exceptionally(ex -> {
                        log.error("Failed to send PlainTextSmsEvent", ex);
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize PlainTextSmsEvent: {}", event, e);
            throw new RuntimeException("Serialization error", e);
        }
    }

    @Async
    public void sendVerified(VerifiedUrlEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(smsProcessedTopic, event.getId(), json)
                    .thenAccept(result -> log.info("Sent VerifiedUrlEvent to topic: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()))
                    .exceptionally(ex -> {
                        log.error("Failed to send VerifiedUrlEvent", ex);
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize VerifiedUrlEvent: {}", event, e);
            throw new RuntimeException("Serialization error", e);
        }
    }
}