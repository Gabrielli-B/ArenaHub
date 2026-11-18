package com.gabrielli.backend.service;

import com.gabrielli.backend.dto.RegistrationRequest;
import com.gabrielli.backend.dto.RegistrationResponse;
import com.gabrielli.backend.entity.Registration;
import com.gabrielli.backend.entity.Team;
import com.gabrielli.backend.entity.Tournament;
import com.gabrielli.backend.enums.TournamentStatus;
import com.gabrielli.backend.exception.BusinessRuleException;
import com.gabrielli.backend.exception.ResourceNotFoundException;
import com.gabrielli.backend.mapper.RegistrationMapper;
import com.gabrielli.backend.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final RegistrationMapper registrationMapper;

    public RegistrationService(RegistrationRepository registrationRepository, TournamentService tournamentService, TeamService teamService, RegistrationMapper registrationMapper) {
        this.registrationRepository = registrationRepository;
        this.tournamentService = tournamentService;
        this.teamService = teamService;
        this.registrationMapper = registrationMapper;
    }

    private void checkRegistrationsOpen(Tournament tournament) {
        if (tournament.getStatus() != TournamentStatus.OPEN) {
            throw new BusinessRuleException("As inscrições deste torneio não estão abertas");
        }
    }

    @Transactional
    public RegistrationResponse registerTeam(Long tournamentId, RegistrationRequest request) {
        Tournament tournament = tournamentService.findEntityById(tournamentId);
        checkRegistrationsOpen(tournament);

        Team team = teamService.findEntityById(request.teamId());

        if (registrationRepository.existsByTournamentIdAndTeamId(tournamentId, team.getId())) {
            throw new BusinessRuleException("A equipe já está inscrita neste torneio");
        }

        // Contagem direta no banco: é a fonte confiável para checar o limite
        long registered = registrationRepository.countByTournamentId(tournamentId);
        if (registered >= tournament.getTeamLimit()) {
            throw new BusinessRuleException("O torneio já atingiu o limite de " + tournament.getTeamLimit() + " equipes");
        }

        Registration registration = new Registration();
        registration.setTournament(tournament);
        registration.setTeam(team);
        Registration saved = registrationRepository.save(registration);

        // Mantém o contador do torneio sincronizado (a entidade é gerenciada, o JPA grava sozinho no fim da transação)
        tournament.setRegistrationCount((int) registered + 1);

        return registrationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> findByTournament(Long tournamentId) {
        tournamentService.findEntityById(tournamentId); // garante o 404 se o torneio não existir
        return registrationMapper.toResponseList(
                registrationRepository.findByTournamentIdOrderByTeamNameAsc(tournamentId));
    }

    @Transactional
    public void removeTeam(Long tournamentId, Long teamId) {
        Tournament tournament = tournamentService.findEntityById(tournamentId);
        checkRegistrationsOpen(tournament);

        Registration registration = registrationRepository.findByTournamentIdAndTeamId(tournamentId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Esta equipe não está inscrita neste torneio"));

        long registered = registrationRepository.countByTournamentId(tournamentId);
        registrationRepository.delete(registration);
        tournament.setRegistrationCount((int) registered - 1);
    }

}
