package com.gabrielli.backend.dto;

public record FieldErrorResponse(
        String field,
        String message
) {
}
