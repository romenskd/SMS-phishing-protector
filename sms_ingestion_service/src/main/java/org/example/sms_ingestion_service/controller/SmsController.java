package org.example.sms_ingestion_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sms_ingestion_service.dto.SmsRequest;
import org.example.sms_ingestion_service.dto.SmsResponse;
import org.example.sms_ingestion_service.exception.SmsValidationException;
import org.example.sms_ingestion_service.service.SmsProcessingService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sms")
@Tag(name = "SMS Ingestion", description = "API for receiving SMS messages")
public class SmsController {
    private final SmsProcessingService processingService;

    @Operation(summary = "Submit SMS message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "SMS accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid SMS data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SmsResponse> receiveSms(@RequestBody @Valid SmsRequest request,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new SmsValidationException(bindingResult);
        }

        processingService.process(request);

        return ResponseEntity.accepted()
                .body(SmsResponse.success("SMS accepted for processing"));

    }
}
