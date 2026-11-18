package com.gabrielli.backend.repository;

import com.gabrielli.backend.entity.Match;
import com.gabrielli.backend.enums.MatchStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match,Long> {
    boolean existsByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    boolean existsByTournamentIdAndMatchStatusNot(Long tournamentId, MatchStatus matchStatus);

    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    List<Match> findByTournamentIdOrderByRoundAscIdAsc(Long tournamentId);

    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    List<Match> findByTournamentIdAndRoundOrderByIdAsc(Long tournamentId, int round);

    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    List<Match> findByTournamentIdAndMatchStatus(Long tournamentId, MatchStatus matchStatus);
}
