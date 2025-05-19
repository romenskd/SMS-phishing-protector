package org.example.sms_ingestion_service.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SmsValidationException extends RuntimeException{

    private final List<String> errors;

    public SmsValidationException(BindingResult bindingResult) {
        super("Validation failed for SMS request");
        this.errors = bindingResult.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

}
