package org.example.phishing_control_service.repository;

import org.example.phishing_control_service.entity.VerifiedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VerifiedUrlJpaRepository extends JpaRepository<VerifiedUrl, Long> {
    Optional<VerifiedUrl> findByUrl(String url);

    @Modifying
    @Query("DELETE FROM VerifiedUrl v WHERE v.expiryAt < CURRENT_TIMESTAMP")
    void deleteExpiredUrls();

    @Query("SELECT v FROM VerifiedUrl v WHERE v.url IN :urls")
    List<VerifiedUrl> findByUrlIn(@Param("urls") List<String> urls);
}
