package org.example.sms_ingestion_service.service.handler;

import lombok.RequiredArgsConstructor;
import org.example.sms_ingestion_service.dto.SmsRequest;
import org.example.sms_ingestion_service.kafka.event.UrlVerificationEvent;
import org.example.sms_ingestion_service.kafka.producer.UrlVerificationKafkaProducer;
import org.example.sms_ingestion_service.service.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UrlVerificationHandler implements SmsMessageHandler {

    private final UrlVerificationKafkaProducer producer;

    private final Pattern URL_PATTERN = Pattern
            .compile("(https?://(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&/=]*))", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean canHandle(SmsRequest request) {

        String message = request.message();

        if (message != null && !message.trim().isEmpty()) {
            Matcher matcher = URL_PATTERN.matcher(message);
            return matcher.find();
        }

        return false;
    }

    @Override
    public void handle(SmsRequest request) {

        UrlVerificationEvent event = UrlVerificationEvent.builder()
                .id(IdGenerator.generateConnectionId(request.sender(), request.recipient()))
                .message(request.message())
                .build();

        producer.send(event);
    }
}
