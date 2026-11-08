package com.gabrielli.backend.mapper;

import com.gabrielli.backend.dto.TournamentRequest;
import com.gabrielli.backend.dto.TournamentResponse;
import com.gabrielli.backend.entity.Tournament;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TournamentMapper {

    public Tournament toEntity(TournamentRequest request) {
        Tournament tournament = new Tournament();
        applyRequest(request, tournament);
        return tournament;
    }

    public void updateEntity(TournamentRequest request, Tournament tournament) {
        applyRequest(request, tournament);
    }

    public TournamentResponse toResponse(Tournament tournament) {
        return new TournamentResponse(
                tournament.getId(),
                tournament.getName(),
                tournament.getModality(),
                tournament.getRegistrationStatus(),
                tournament.getTeamLimit(),
                tournament.getRegistrationCount(),
                tournament.getCreatedAt()
        );
    }

    public List<TournamentResponse> toResponseList(List<Tournament> tournaments) {
        return tournaments.stream().map(this::toResponse).toList();
    }

    private void applyRequest(TournamentRequest request, Tournament tournament) {
        tournament.setName(request.name().trim());
        tournament.setModality(request.modality());
        tournament.setTeamLimit(request.teamLimit());
    }
}