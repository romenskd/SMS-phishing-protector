package org.example.phishing_control_service.service;

import lombok.RequiredArgsConstructor;
import org.example.phishing_control_service.entity.VerifiedUrl;
import org.example.phishing_control_service.repository.VerifiedUrlJpaRepository;
import org.example.phishing_control_service.repository.jdbc.VerifiedUrlJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlPersistenceService {
    private final VerifiedUrlJdbcRepository jdbcRepository;
    private final VerifiedUrlJpaRepository jpaRepository;
    private final UrlCacheService urlCacheService;

    @Transactional
    public void saveBatch(List<VerifiedUrl> batch) {
        jdbcRepository.saveBatch(batch);

        for (VerifiedUrl verifiedUrl : batch) {
            urlCacheService.put(verifiedUrl.getUrl(), verifiedUrl.getSafe());
        }
    }

    @Transactional
    public VerifiedUrl findByUrl(String url) {
        Boolean cachedSafe = urlCacheService.get(url);
        if (cachedSafe != null) {
            VerifiedUrl cached = new VerifiedUrl();
            cached.setUrl(url);
            cached.setSafe(cachedSafe);
            return cached;
        }

        VerifiedUrl verifiedUrl = jpaRepository.findByUrl(url).orElse(null);
        if (verifiedUrl != null) {
            verifiedUrl.setLastAccessed(OffsetDateTime.now());
            verifiedUrl.setExpiryAt(OffsetDateTime.now().plusDays(30));
            jpaRepository.save(verifiedUrl);

            urlCacheService.put(verifiedUrl.getUrl(), verifiedUrl.getSafe());
        }
        return verifiedUrl;
    }
}