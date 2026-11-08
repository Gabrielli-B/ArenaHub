package com.gabrielli.backend.dto;

import com.gabrielli.backend.enums.Modality;
import jakarta.validation.constraints.*;

public record TournamentRequest(

        @NotBlank(message = "O nome do torneio é obrigatório")
        @Size(min = 3,max = 60, message = "O nome do torneio deve ter entre 3 a 60 caracteres")
        String name,

        @NotNull(message = "A modalidade é obrigatória")
        Modality modality,

        @Min(value = 4, message = "O limite mínimo deve ser de 4 equipes")
        @Max(value = 10, message = "O limite máximo deve ser de 10 equipes")
        @NotNull(message = "O limite de equipes é obrigatório")
        Integer teamLimit
) { }
