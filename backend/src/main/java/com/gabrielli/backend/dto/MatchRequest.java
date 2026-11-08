package com.gabrielli.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MatchRequest(
        @NotNull(message = "O placar do mandante é obrigatório")
        @Min(value = 0, message = "O placar do mandante não pode ser negativo")
        Integer homeTeamScore,

        @NotNull(message = "O placar do visitante é obrigatório")
        @Min(value = 0, message = "O placar do visitante não pode ser negativo")
        Integer awayTeamScore

) { }
