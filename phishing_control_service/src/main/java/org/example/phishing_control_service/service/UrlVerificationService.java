package org.example.phishing_control_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.phishing_control_service.client.WebRiskApiClient;
import org.example.phishing_control_service.entity.VerifiedUrl;
import org.example.phishing_control_service.kafka.event.UrlVerificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlVerificationService {

    @Value("${webrisk.api.token}")
    private String bearerToken;

    @Value("${url-verification.batch-size}")
    private int batchSize;

    private final WebRiskApiClient webRiskApiClient;
    private final UrlPersistenceService urlPersistenceService;

    private final Pattern URL_PATTERN = Pattern
            .compile("(https?://(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&/=]*))",
                    Pattern.CASE_INSENSITIVE);


    public boolean verifyIfSafe(UrlVerificationEvent event) {
        List<String> urls = extractUrls(event.getMessage());
        List<VerifiedUrl> batch = new ArrayList<>(batchSize);
        boolean allSafe = true;

        for (String url : urls) {
            try {
                VerifiedUrl verifiedUrl = urlPersistenceService.findByUrl(url);

                if (verifiedUrl == null) {
                    WebRiskApiClient.EvaluateUriRequest request = new WebRiskApiClient.EvaluateUriRequest(
                            url,
                            List.copyOf(EnumSet.of(
                                    WebRiskApiClient.ThreatType.MALWARE,
                                    WebRiskApiClient.ThreatType.SOCIAL_ENGINEERING,
                                    WebRiskApiClient.ThreatType.UNWANTED_SOFTWARE)),
                            true
                    );

                    WebRiskApiClient.EvaluateUriResponse response =
                            webRiskApiClient.evaluateUri(bearerToken, request);

                    verifiedUrl = createVerifiedUrl(url, response);
                }

                if (!verifiedUrl.getSafe()) {
                    allSafe = false;
                }

                batch.add(verifiedUrl);

                if (batch.size() >= batchSize) {
                    urlPersistenceService.saveBatch(new ArrayList<>(batch));
                    batch.clear();
                }

            } catch (Exception e) {
                log.error("Failed to verify URL: {}", url, e);
                allSafe = false;
            }
        }

        if (!batch.isEmpty()) {
            urlPersistenceService.saveBatch(batch);
        }

        return allSafe;
    }


    private List<String> extractUrls(String message) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(message);

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }

    private VerifiedUrl createVerifiedUrl(String url,
                                          WebRiskApiClient.EvaluateUriResponse response) {
        boolean isSafe = response.scores().stream()
                .noneMatch(score -> isHighRisk(score.confidenceLevel()));

        VerifiedUrl verifiedUrl = new VerifiedUrl();
        verifiedUrl.setUrl(url);
        verifiedUrl.setSafe(isSafe);

        return verifiedUrl;
    }

    private boolean isHighRisk(WebRiskApiClient.ConfidenceLevel confidenceLevel) {
        return confidenceLevel == WebRiskApiClient.ConfidenceLevel.VERY_HIGH ||
                confidenceLevel == WebRiskApiClient.ConfidenceLevel.HIGH ||
                confidenceLevel == WebRiskApiClient.ConfidenceLevel.MEDIUM ||
                confidenceLevel == WebRiskApiClient.ConfidenceLevel.EXTREMELY_HIGH;
    }
}
