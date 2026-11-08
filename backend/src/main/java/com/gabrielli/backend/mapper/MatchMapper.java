package com.gabrielli.backend.mapper;

import com.gabrielli.backend.dto.MatchResponse;
import com.gabrielli.backend.entity.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final TeamMapper teamMapper;

    public MatchResponse toResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getRound(),
                teamMapper.toResponse(match.getHomeTeam()),
                teamMapper.toResponse(match.getAwayTeam()),
                match.getHomeTeamScore(),
                match.getAwayTeamScore(),
                match.getMatchStatus()
        );
    }

    public List<MatchResponse> toResponseList(List<Match> matches) {
        return matches.stream().map(this::toResponse).toList();
    }
}
