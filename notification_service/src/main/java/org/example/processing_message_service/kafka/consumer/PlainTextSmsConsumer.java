package org.example.processing_message_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.processing_message_service.kafka.event.PlainTextSmsEvent;
import org.example.processing_message_service.kafka.producer.SmsProcessedEventProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
public class PlainTextSmsConsumer {
    private final SmsProcessedEventProducer smsProcessedEventProducer;
    private final ObjectMapper objectMapper;
    private final TaskExecutor kafkaConsumerTaskExecutor;

    public PlainTextSmsConsumer(SmsProcessedEventProducer smsProcessedEventProducer, ObjectMapper objectMapper,
                                @Qualifier("kafkaConsumerTaskExecutor") TaskExecutor kafkaConsumerTaskExecutor) {
        this.smsProcessedEventProducer = smsProcessedEventProducer;
        this.objectMapper = objectMapper;
        this.kafkaConsumerTaskExecutor = kafkaConsumerTaskExecutor;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.plain-text}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        CompletableFuture.runAsync(() -> {
            try {
                PlainTextSmsEvent event = objectMapper.readValue(record.value(), PlainTextSmsEvent.class);

                processEvent(event);
                ack.acknowledge();
            } catch (Exception e) {
                log.error("Failed to process PlainTextSmsEvent: {}", record.value(), e);
            }
        }, kafkaConsumerTaskExecutor);
    }

    private void processEvent(PlainTextSmsEvent event) {
        log.info("Received PlainTextSmsEvent: {}", event);
        smsProcessedEventProducer.sendPlainText(event);
    }
}