package org.example.user_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.user_service.kafka.event.VerifiedUrlEvent;
import org.example.user_service.service.SmsPersistenceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class VerifiedUrlConsumer {

    private final ObjectMapper objectMapper;
    private final TaskExecutor kafkaConsumerTaskExecutor;
    private final SmsPersistenceService smsPersistenceService;

    public VerifiedUrlConsumer(ObjectMapper objectMapper,
                               @Qualifier("kafkaConsumerTaskExecutor") TaskExecutor kafkaConsumerTaskExecutor,
                               SmsPersistenceService smsPersistenceService) {
        this.objectMapper = objectMapper;
        this.kafkaConsumerTaskExecutor = kafkaConsumerTaskExecutor;
        this.smsPersistenceService = smsPersistenceService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.verified-url}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        CompletableFuture.runAsync(() -> {
            try {
                VerifiedUrlEvent event = objectMapper.readValue(record.value(), VerifiedUrlEvent.class);

                processEvent(event);
                ack.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process VerifiedUrlSmsEvent: {}", record.value(), e);
            }
        }, kafkaConsumerTaskExecutor);
    }

    private void processEvent(VerifiedUrlEvent event) {
        log.info("Received VerifiedUrlSmsEvent: {}", event);
        smsPersistenceService.saveVerifiedUrlEvent(event);
    }
}