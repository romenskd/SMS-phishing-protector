package org.example.phishing_control_service.client;


import org.example.phishing_control_service.config.WebRiskFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "webrisk-api",
        url = "https://webrisk.googleapis.com",
        configuration = WebRiskFeignConfig.class
)
public interface WebRiskApiClient {
    @PostMapping(
            value = "/v1eap1:evaluateUri",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    EvaluateUriResponse evaluateUri(
            @RequestHeader("Authorization") String authorization,
            @RequestBody EvaluateUriRequest request
    );
    record EvaluateUriRequest(
            String uri,
            List<ThreatType> threatTypes,
            Boolean allowScan
    ) {}

    record EvaluateUriResponse(
            List<Score> scores
    ) {}

    record Score(
            ThreatType threatType,
            ConfidenceLevel confidenceLevel
    ) {}

    enum ThreatType {
        THREAT_TYPE_UNSPECIFIED,
        SOCIAL_ENGINEERING,
        MALWARE,
        UNWANTED_SOFTWARE
    }

    enum ConfidenceLevel {
        CONFIDENCE_LEVEL_UNSPECIFIED,
        SAFE,
        LOW,
        MEDIUM,
        HIGH,
        HIGHER,
        VERY_HIGH,
        EXTREMELY_HIGH
    }
}
