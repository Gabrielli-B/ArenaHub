package com.gabrielli.backend.dto;

import com.gabrielli.backend.enums.MatchStatus;

public record MatchResponse(
        Long id,
        int round,
        TeamResponse homeTeam,
        TeamResponse awayTeam,
        int homeTeamScore,
        int awayTeamScore,
        MatchStatus matchStatus
) {
}
