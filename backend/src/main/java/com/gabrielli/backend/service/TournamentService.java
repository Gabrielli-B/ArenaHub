package com.gabrielli.backend.service;

import com.gabrielli.backend.dto.TournamentRequest;
import com.gabrielli.backend.dto.TournamentResponse;
import com.gabrielli.backend.entity.Tournament;
import com.gabrielli.backend.enums.RegistrationStatus;
import com.gabrielli.backend.exception.BusinessRuleException;
import com.gabrielli.backend.exception.ResourceNotFoundException;
import com.gabrielli.backend.mapper.TournamentMapper;
import com.gabrielli.backend.repository.RegistrationRepository;
import com.gabrielli.backend.repository.TournamentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final RegistrationRepository registrationRepository;
    private final TournamentMapper tournamentMapper;


    public TournamentService(TournamentRepository tournamentRepository, RegistrationRepository registrationRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.registrationRepository = registrationRepository;
        this.tournamentMapper = tournamentMapper;
    }

    //funcoes auxiliares

    public Tournament findEntityById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneio não encontrado com id " + id));
    }

    private void checkIsOpen(Tournament tournament, String message) {
        if (tournament.getRegistrationStatus() != RegistrationStatus.OPEN) {
            throw new BusinessRuleException(message);
        }
    }

    //funcoes essenciais
    @Transactional
    public TournamentResponse createTournament(TournamentRequest request) {
        Tournament tournament = tournamentMapper.toEntity(request);

        tournament.setRegistrationStatus(RegistrationStatus.OPEN);

        return tournamentMapper.toResponse(tournamentRepository.save(tournament));
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> findAllTournaments() {
        return tournamentMapper.toResponseList(
                tournamentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public TournamentResponse findById(Long id) {
        return tournamentMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public TournamentResponse update(Long id, TournamentRequest request) {
        Tournament tournament = findEntityById(id);

        checkIsOpen(tournament, "Só é possível editar torneios com inscrições abertas");

        if (request.teamLimit() < tournament.getRegistrationCount()) {
            throw new BusinessRuleException("O limite de equipes não pode ser menor que o número de equipes já inscritas ("
                    + tournament.getRegistrationCount() + ")");
        }

        tournamentMapper.updateEntity(request, tournament);
        return tournamentMapper.toResponse(tournamentRepository.save(tournament));
    }

    @Transactional
    public void delete(Long id) {
        Tournament tournament = findEntityById(id);

        checkIsOpen(tournament, "Só é possível excluir torneios com inscrições abertas");

        registrationRepository.deleteByTournamentId(id);
        tournamentRepository.delete(tournament);
    }


}
