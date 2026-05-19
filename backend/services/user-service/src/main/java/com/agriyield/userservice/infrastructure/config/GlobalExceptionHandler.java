package com.agriyield.userservice.infrastructure.config;

import com.agriyield.userservice.core.domain.exceptions.BusinessException;
import com.agriyield.userservice.core.domain.exceptions.ResourceNotFoundException;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("Validation errors: {}", errors);
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(
            "Validation failed",
            "VALIDATION_ERROR"
        );
        response.setData(errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ApiResponse<Void> response = ApiResponse.error(
            "An unexpected error occurred. Please try again later.",
            "INTERNAL_SERVER_ERROR"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    private HttpStatus determineHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "INVALID_PHONE", "INVALID_OTP", "OTP_EXPIRED", "INVALID_PASSWORD", 
                 "WEAK_PASSWORD", "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
            case "AUTH_FAILED", "INVALID_REFRESH_TOKEN" -> HttpStatus.UNAUTHORIZED;
            case "ACCOUNT_SUSPENDED", "ACCOUNT_LOCKED" -> HttpStatus.FORBIDDEN;
            case "DUPLICATE_PHONE", "DUPLICATE_FAYDA_ID" -> HttpStatus.CONFLICT;
            case "RESOURCE_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
