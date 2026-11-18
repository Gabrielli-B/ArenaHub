package com.gabrielli.backend.service;

import com.gabrielli.backend.dto.ClassificationResponse;
import com.gabrielli.backend.dto.MatchRequest;
import com.gabrielli.backend.dto.MatchResponse;
import com.gabrielli.backend.entity.Match;
import com.gabrielli.backend.entity.Registration;
import com.gabrielli.backend.entity.Team;
import com.gabrielli.backend.entity.Tournament;
import com.gabrielli.backend.enums.MatchStatus;
import com.gabrielli.backend.enums.TournamentStatus;
import com.gabrielli.backend.exception.BusinessRuleException;
import com.gabrielli.backend.exception.ResourceNotFoundException;
import com.gabrielli.backend.mapper.MatchMapper;
import com.gabrielli.backend.mapper.TeamMapper;
import com.gabrielli.backend.repository.MatchRepository;
import com.gabrielli.backend.repository.RegistrationRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MatchService {

    private static final int MIN_TEAMS_TO_START = 4;
    private static final int POINTS_PER_WIN = 3;
    private static final int POINTS_PER_DRAW = 1;

    private final MatchRepository matchRepository;
    private final RegistrationRepository registrationRepository;
    private final TournamentService tournamentService;
    private final MatchMapper matchMapper;
    private final TeamMapper teamMapper;

    public MatchService(MatchRepository matchRepository, RegistrationRepository registrationRepository, TournamentService tournamentService, MatchMapper matchMapper, TeamMapper teamMapper) {
        this.matchRepository = matchRepository;
        this.registrationRepository = registrationRepository;
        this.tournamentService = tournamentService;
        this.matchMapper = matchMapper;
        this.teamMapper = teamMapper;
    }

    @Transactional
    public List<MatchResponse> startTournament(Long tournamentId) {
        Tournament tournament = tournamentService.findEntityById(tournamentId);

        if (tournament.getStatus() != TournamentStatus.OPEN) {
            throw new BusinessRuleException("O torneio já foi iniciado ou encerrado");
        }

        List<Team> teams = registrationRepository.findByTournamentIdOrderByTeamNameAsc(tournamentId)
                .stream()
                .map(Registration::getTeam)
                .toList();

        if (teams.size() < MIN_TEAMS_TO_START) {
            throw new BusinessRuleException("O torneio precisa de no mínimo " + MIN_TEAMS_TO_START
                    + " equipes inscritas para ser iniciado");
        }

        List<Match> matches = generateRoundRobin(tournament, teams);
        matchRepository.saveAll(matches);

        tournament.setStatus(TournamentStatus.IN_PROGRESS);

        return matchMapper.toResponseList(matches);
    }

    // Método do círculo: a primeira equipe fica fixa e as outras giram a cada rodada.
    private List<Match> generateRoundRobin(Tournament tournament, List<Team> teams) {
        List<Team> rotation = new ArrayList<>(teams);
        if (rotation.size() % 2 != 0) {
            rotation.add(null); // "folga" fictícia: quem enfrenta null descansa na rodada
        }

        int size = rotation.size();
        int totalRounds = size - 1;
        List<Match> matches = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {
            for (int i = 0; i < size / 2; i++) {
                Team first = rotation.get(i);
                Team second = rotation.get(size - 1 - i);

                if (first == null || second == null) {
                    continue;
                }

                // Alterna quem joga em casa para equilibrar os mandos
                boolean firstIsHome = (round + i) % 2 == 0;
                Team home = firstIsHome ? first : second;
                Team away = firstIsHome ? second : first;

                matches.add(createMatch(tournament, round, home, away));
            }
            // Gira: o último vai para a posição 1 (a posição 0 continua fixa)
            rotation.add(1, rotation.remove(size - 1));
        }
        return matches;
    }

    private Match createMatch(Tournament tournament, int round, Team home, Team away) {
        Match match = new Match();
        match.setTournament(tournament);
        match.setRound(round);
        match.setHomeTeam(home);
        match.setAwayTeam(away);
        match.setMatchStatus(MatchStatus.SCHEDULED);
        return match;
    }

    // ---------------------------------------------------------------- partidas

    @Transactional(readOnly = true)
    public List<MatchResponse> findMatchesByTournament(Long tournamentId, Integer round) {
        tournamentService.findEntityById(tournamentId); // garante o 404 se o torneio não existir

        List<Match> matches = (round == null)
                ? matchRepository.findByTournamentIdOrderByRoundAscIdAsc(tournamentId)
                : matchRepository.findByTournamentIdAndRoundOrderByIdAsc(tournamentId, round);

        return matchMapper.toResponseList(matches);
    }

    @Transactional
    public MatchResponse registerResult(Long matchId, MatchRequest request) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com id " + matchId));

        Tournament tournament = match.getTournament();

        if (tournament.getStatus() != TournamentStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Só é possível lançar resultados em torneios em andamento");
        }

        // Permite corrigir o placar de uma partida já finalizada, enquanto o torneio estiver em andamento
        match.setHomeTeamScore(request.homeTeamScore());
        match.setAwayTeamScore(request.awayTeamScore());
        match.setMatchStatus(MatchStatus.FINISHED);

        // Grava agora para a consulta abaixo já enxergar este resultado
        matchRepository.saveAndFlush(match);

        boolean hasPendingMatches = matchRepository
                .existsByTournamentIdAndMatchStatusNot(tournament.getId(), MatchStatus.FINISHED);
        if (!hasPendingMatches) {
            tournament.setStatus(TournamentStatus.FINISHED);
        }

        return matchMapper.toResponse(match);
    }

    // ---------------------------------------------------------------- classificação

    @Transactional(readOnly = true)
    public List<ClassificationResponse> getClassification(Long tournamentId) {
        tournamentService.findEntityById(tournamentId); // garante o 404 se o torneio não existir

        // Começa com uma linha zerada para cada equipe inscrita (mesmo as sem jogos)
        Map<Long, Standing> table = new LinkedHashMap<>();
        for (Registration registration : registrationRepository.findByTournamentIdOrderByTeamNameAsc(tournamentId)) {
            Team team = registration.getTeam();
            table.put(team.getId(), new Standing(team));
        }

        // Só partidas finalizadas entram no cálculo
        for (Match match : matchRepository.findByTournamentIdAndMatchStatus(tournamentId, MatchStatus.FINISHED)) {
            int homeScore = match.getHomeTeamScore();
            int awayScore = match.getAwayTeamScore();
            table.get(match.getHomeTeam().getId()).addResult(homeScore, awayScore);
            table.get(match.getAwayTeam().getId()).addResult(awayScore, homeScore);
        }

        // Desempate: pontos, vitórias, saldo de gols, gols pró e, por fim, nome
        Comparator<Standing> order = Comparator
                .comparingInt(Standing::getPoints).reversed()
                .thenComparing(Comparator.comparingInt(Standing::getWins).reversed())
                .thenComparing(Comparator.comparingInt(Standing::getScoreDifference).reversed())
                .thenComparing(Comparator.comparingInt(Standing::getScoreFor).reversed())
                .thenComparing(standing -> standing.getTeam().getName());

        List<Standing> sorted = new ArrayList<>(table.values());
        sorted.sort(order);

        List<ClassificationResponse> classification = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Standing s = sorted.get(i);
            classification.add(new ClassificationResponse(
                    i + 1,
                    teamMapper.toResponse(s.getTeam()),
                    s.getPoints(),
                    s.getGames(),
                    s.getWins(),
                    s.getDraws(),
                    s.getLosses(),
                    s.getScoreFor(),
                    s.getScoreAgainst(),
                    s.getScoreDifference()
            ));
        }
        return classification;
    }

    // Linha da tabela de classificação, usada só durante o cálculo
    @Getter
    private static class Standing {
        private final Team team;
        private int points;
        private int games;
        private int wins;
        private int draws;
        private int losses;
        private int scoreFor;
        private int scoreAgainst;

        Standing(Team team) {
            this.team = team;
        }

        void addResult(int scored, int conceded) {
            games++;
            scoreFor += scored;
            scoreAgainst += conceded;
            if (scored > conceded) {
                wins++;
                points += POINTS_PER_WIN;
            } else if (scored == conceded) {
                draws++;
                points += POINTS_PER_DRAW;
            } else {
                losses++;
            }
        }

        int getScoreDifference() {
            return scoreFor - scoreAgainst;
        }
    }


}
