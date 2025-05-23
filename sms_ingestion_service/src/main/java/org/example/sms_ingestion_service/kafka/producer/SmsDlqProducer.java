package org.example.sms_ingestion_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sms_ingestion_service.dto.SmsRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Lazy
@RequiredArgsConstructor
@Slf4j
public class SmsDlqProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String dlqTopic = "sms-dlq";

    @Async
    public void sendToDlq(SmsRequest smsRequest) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(smsRequest);
            kafkaTemplate.send(dlqTopic, smsRequest.sender(), jsonRequest)
                    .thenAccept(result -> log.info("Sent SmsRequest as JSON to DLQ topic: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().offset()))
                    .exceptionally(ex -> {
                        log.error("Failed to send SmsRequest to DLQ", ex);
                        return null;
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SmsRequest to JSON: {}", smsRequest, e);
            throw new RuntimeException("Error serializing SmsRequest to JSON", e);
        }

    }
}