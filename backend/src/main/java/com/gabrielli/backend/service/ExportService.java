package com.gabrielli.backend.service;

import com.gabrielli.backend.dto.TeamResponse;
import com.gabrielli.backend.dto.TournamentExportResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExportService {
    private final TournamentService tournamentService;
    private final RegistrationService registrationService;
    private final MatchService matchService;
    private final TeamService teamService;

    public ExportService(TournamentService tournamentService, RegistrationService registrationService, MatchService matchService, TeamService teamService) {
        this.tournamentService = tournamentService;
        this.registrationService = registrationService;
        this.matchService = matchService;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public TournamentExportResponse exportTournament(Long tournamentId) {
        return new TournamentExportResponse(
                tournamentService.findById(tournamentId),
                registrationService.findByTournament(tournamentId),
                matchService.getClassification(tournamentId),
                matchService.findMatchesByTournament(tournamentId, null)
        );
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> exportTeams() {return teamService.findAllTeams();}
}
