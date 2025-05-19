package org.example.phishing_control_service.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.example.phishing_control_service.entity.VerifiedUrl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VerifiedUrlJdbcRepositoryImpl implements VerifiedUrlJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveBatch(List<VerifiedUrl> urls) {
        String sql = """
            INSERT INTO tracked_urls (url, is_safe, created_at, last_accessed, expiry_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (url) DO UPDATE SET
                is_safe = EXCLUDED.is_safe,
                last_accessed = EXCLUDED.last_accessed,
                expiry_at = EXCLUDED.expiry_at
        """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                VerifiedUrl url = urls.get(i);
                ps.setString(1, url.getUrl());
                ps.setBoolean(2, url.getSafe() != null && url.getSafe());
                ps.setObject(3, url.getCreatedAt());
                ps.setObject(4, url.getLastAccessed());
                ps.setObject(5, url.getExpiryAt());
            }

            @Override
            public int getBatchSize() {
                return urls.size();
            }
        });
    }
}
