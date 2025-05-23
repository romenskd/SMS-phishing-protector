package org.example.user_service.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.example.user_service.entity.SmsMessage;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SmsMessageJdbcRepositoryImpl implements SmsMessageJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "INSERT INTO sms_message " +
            "(id, message, is_allowed, is_safe, received_at, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void saveAllBatch(List<SmsMessage> messages) {
        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SmsMessage msg = messages.get(i);
                ps.setString(1, msg.getId());
                ps.setString(2, msg.getMessage());
                ps.setBoolean(3, msg.isAllowed());
                ps.setBoolean(4, msg.isSafe());
                ps.setTimestamp(5, Timestamp.from(msg.getReceivedAt()));
                ps.setTimestamp(6, msg.getCreatedAt() != null ? Timestamp.from(msg.getCreatedAt()) : Timestamp.from(Instant.now()));
                ps.setTimestamp(7, msg.getUpdatedAt() != null ? Timestamp.from(msg.getUpdatedAt()) : Timestamp.from(Instant.now()));
            }

            @Override
            public int getBatchSize() {
                return messages.size();
            }
        });
    }
}