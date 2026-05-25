package com.agriyield.userservice.domain.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " not found with " + field + ": " + value, "RESOURCE_NOT_FOUND");
    }
}
