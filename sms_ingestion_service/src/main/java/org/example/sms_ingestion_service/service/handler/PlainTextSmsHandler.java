package org.example.sms_ingestion_service.service.handler;

import lombok.RequiredArgsConstructor;
import org.example.sms_ingestion_service.dto.SmsRequest;
import org.example.sms_ingestion_service.kafka.event.PlainTextSmsEvent;
import org.example.sms_ingestion_service.kafka.producer.PlainTextSmsKafkaProducer;
import org.example.sms_ingestion_service.service.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class PlainTextSmsHandler implements SmsMessageHandler{

    private final PlainTextSmsKafkaProducer kafkaProducer;
    private static final Pattern URL_PATTERN = Pattern
            .compile("(https?://(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&/=]*))",
                    Pattern.CASE_INSENSITIVE);

    @Override
    public boolean canHandle(SmsRequest request) {
        String message = request.message();

        String lowerCaseMessage = message.toLowerCase().trim();
        if (lowerCaseMessage.equals("start") || lowerCaseMessage.equals("stop")) {
            return false;
        }

        Matcher urlMatcher = URL_PATTERN.matcher(message);
        if (urlMatcher.find()) {
            return false;
        }

        return true;
    }

    @Override
    public void handle(SmsRequest request) {
        PlainTextSmsEvent event = PlainTextSmsEvent.builder()
                .id(IdGenerator.generateConnectionId(request.sender(), request.recipient()))
                .message(request.message())
                .build();

        kafkaProducer.send(event);
    }
}
