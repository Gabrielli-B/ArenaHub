package com.gabrielli.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<FieldErrorResponse> fields,
        LocalDateTime timestamp
) {
}
