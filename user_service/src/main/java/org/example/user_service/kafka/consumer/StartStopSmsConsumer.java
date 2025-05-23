package org.example.user_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.user_service.kafka.event.StartStopSmsEvent;
import org.example.user_service.service.SmsPersistenceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class StartStopSmsConsumer {

    private final ObjectMapper objectMapper;
    private final TaskExecutor kafkaConsumerTaskExecutor;
    private final SmsPersistenceService smsPersistenceService;

    public StartStopSmsConsumer(ObjectMapper objectMapper,
                                @Qualifier("kafkaConsumerTaskExecutor") TaskExecutor kafkaConsumerTaskExecutor,
                                SmsPersistenceService smsPersistenceService) {
        this.objectMapper = objectMapper;
        this.kafkaConsumerTaskExecutor = kafkaConsumerTaskExecutor;
        this.smsPersistenceService = smsPersistenceService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.start-stop}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        CompletableFuture.runAsync(() -> {
            try {
                StartStopSmsEvent event = objectMapper.readValue(record.value(), StartStopSmsEvent.class);

                processEvent(event);
                ack.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process StartStopSmsEvent: {}", record.value(), e);
            }
        }, kafkaConsumerTaskExecutor);
    }

    private void processEvent(StartStopSmsEvent event) {
        log.info("Received StartStopSmsEvent: {}", event);
        smsPersistenceService.saveStartStopEvent(event);
    }
}