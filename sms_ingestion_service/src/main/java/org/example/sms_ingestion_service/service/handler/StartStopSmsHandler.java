package org.example.sms_ingestion_service.service.handler;

import lombok.RequiredArgsConstructor;
import org.example.sms_ingestion_service.dto.SmsRequest;
import org.example.sms_ingestion_service.kafka.event.StartStopSmsEvent;
import org.example.sms_ingestion_service.kafka.producer.StartStopKafkaProducer;
import org.example.sms_ingestion_service.service.IdGenerator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartStopSmsHandler implements SmsMessageHandler {

    private final StartStopKafkaProducer startStopKafkaProducer;

    @Override
    public boolean canHandle(SmsRequest request) {
        String message = request.message().toLowerCase().trim();
        return message.equals("start") || message.equals("stop");
    }

    @Override
    public void handle(SmsRequest request) {

        StartStopSmsEvent event = StartStopSmsEvent.builder()
                .id(IdGenerator.generateConnectionId(request.sender(), request.recipient()))
                .message(request.message())
                .build();

        startStopKafkaProducer.send(event);

    }
}
