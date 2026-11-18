package com.gabrielli.backend.dto;


import com.gabrielli.backend.enums.Modality;
import com.gabrielli.backend.enums.RegistrationStatus;
import com.gabrielli.backend.enums.TournamentStatus;

import java.time.LocalDateTime;

public record TournamentResponse(
        Long id,
        String name,
        Modality modality,
        TournamentStatus status,
        int teamLimit,
        int registrationCount,
        LocalDateTime createdAt
) { }
