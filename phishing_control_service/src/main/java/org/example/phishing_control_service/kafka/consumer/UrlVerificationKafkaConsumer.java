package org.example.phishing_control_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.phishing_control_service.kafka.event.UrlVerificationEvent;
import org.example.phishing_control_service.kafka.event.VerifiedUrlEvent;
import org.example.phishing_control_service.kafka.producer.VerifiedUrlProducer;
import org.example.phishing_control_service.service.UrlVerificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class UrlVerificationKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final UrlVerificationService urlVerificationService;
    private final VerifiedUrlProducer producer;

    @Qualifier("kafkaConsumerTaskExecutor")
    private final TaskExecutor kafkaConsumerTaskExecutor;

    public UrlVerificationKafkaConsumer(ObjectMapper objectMapper,
                                        UrlVerificationService urlVerificationService, VerifiedUrlProducer producer,
                                        TaskExecutor kafkaConsumerTaskExecutor,
                                        KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.urlVerificationService = urlVerificationService;
        this.producer = producer;
        this.kafkaConsumerTaskExecutor = kafkaConsumerTaskExecutor;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.url-verification}",
            containerFactory = "urlVerificationKafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        CompletableFuture.runAsync(() -> {
            try {
                UrlVerificationEvent event = objectMapper.readValue(record.value(), UrlVerificationEvent.class);

                processEvent(event);

                ack.acknowledge();
            } catch (Exception e) {
                log.error("Processing failed for record: {}", record.value(), e);
            }
        }, kafkaConsumerTaskExecutor);
    }

    private void processEvent(UrlVerificationEvent event) {
        boolean isSafe = urlVerificationService.verifyIfSafe(event);
        if (!isSafe) {
            log.warn("Unsafe URLs detected: {}", event.getMessage());
        }

        VerifiedUrlEvent verifiedUrlEvent = VerifiedUrlEvent.builder()
                .id(event.getId())
                .isSafe(isSafe)
                .message(event.getMessage())
                .build();

        producer.send(verifiedUrlEvent);

    }
}