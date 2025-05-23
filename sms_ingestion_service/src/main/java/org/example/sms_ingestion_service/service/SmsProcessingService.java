package org.example.sms_ingestion_service.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sms_ingestion_service.dto.SmsRequest;
import org.example.sms_ingestion_service.kafka.producer.SmsDlqProducer;
import org.example.sms_ingestion_service.service.handler.SmsMessageHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsProcessingService {
    private final List<SmsMessageHandler> handlers;
    private final MeterRegistry meterRegistry;

    private final SmsDlqProducer dlqProducer;

    @Retry(name = "smsProcessingRetry", fallbackMethod = "fallbackProcessing")
    @CircuitBreaker(name = "smsProcessingCircuitBreaker", fallbackMethod = "fallbackProcessing")
    @Timed(value = "sms.processing.time", description = "Time taken to process SMS")
    public void process(SmsRequest smsRequest) {
        meterRegistry.counter("sms.received").increment();

        handlers.stream()
                .filter(handler -> handler.canHandle(smsRequest))
                .findFirst()
                .ifPresent(handler -> handler.handle(smsRequest));
    }

    private void fallbackProcessing(SmsRequest smsRequest, Exception e) {
        log.error("Fallback processing for SMS from {}", smsRequest.sender(), e);
        meterRegistry.counter("sms.processing.failures").increment();

    }
}
