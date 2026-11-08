package com.gabrielli.backend.mapper;

import com.gabrielli.backend.dto.TeamRequest;
import com.gabrielli.backend.dto.TeamResponse;
import com.gabrielli.backend.entity.Team;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class TeamMapper {

    public Team toEntity(TeamRequest request) {
        Team team = new Team();
        applyRequest(request, team);
        return team;
    }

    public void updateEntity(TeamRequest request, Team team) {
        applyRequest(request, team);
    }

    public TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getTeamAcronym(),
                team.getCity()
        );
    }

    public List<TeamResponse> toResponseList(List<Team> teams) {
        return teams.stream().map(this::toResponse).toList();
    }

    private void applyRequest(TeamRequest request, Team team) {
        team.setName(request.name().trim());
        team.setTeamAcronym(request.teamAcronym().trim().toUpperCase(Locale.ROOT));
        team.setCity(request.city());
    }
}