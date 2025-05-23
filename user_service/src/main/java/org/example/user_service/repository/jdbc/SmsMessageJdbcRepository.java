package org.example.user_service.repository.jdbc;

import org.example.user_service.entity.SmsMessage;

import java.util.List;

public interface SmsMessageJdbcRepository {
    void saveAllBatch(List<SmsMessage> messages);
}
