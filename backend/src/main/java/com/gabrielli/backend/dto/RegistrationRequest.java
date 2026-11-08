package com.gabrielli.backend.dto;

import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
        @NotNull(message = "O ID da equipe é obrigatório")
        Long teamId
) { }
