package com.gabrielli.backend.dto;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long id,
        TeamResponse team,
        LocalDateTime createdAt
) { }
