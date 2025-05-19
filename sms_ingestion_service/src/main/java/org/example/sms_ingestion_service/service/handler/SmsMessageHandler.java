package org.example.sms_ingestion_service.service.handler;

import org.example.sms_ingestion_service.dto.SmsRequest;

public interface SmsMessageHandler {
    boolean canHandle(SmsRequest request);
    void handle(SmsRequest request);
}
