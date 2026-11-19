package com.gabrielli.backend.dto;

import java.util.List;

public record TournamentExportResponse(
        TournamentResponse tournament,
        List<RegistrationResponse> participants,
        List<ClassificationResponse> classification,
        List<MatchResponse> matches
) { }
