package org.example.phishing_control_service.repository.jdbc;

import org.example.phishing_control_service.entity.VerifiedUrl;

import java.util.List;

public interface VerifiedUrlJdbcRepository {
    void saveBatch(List<VerifiedUrl> urls);
}
