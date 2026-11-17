package com.gabrielli.backend.service;

import com.gabrielli.backend.dto.TeamRequest;
import com.gabrielli.backend.dto.TeamResponse;
import com.gabrielli.backend.entity.Team;
import com.gabrielli.backend.exception.BusinessRuleException;
import com.gabrielli.backend.exception.ResourceNotFoundException;
import com.gabrielli.backend.mapper.TeamMapper;
import com.gabrielli.backend.repository.MatchRepository;
import com.gabrielli.backend.repository.RegistrationRepository;
import com.gabrielli.backend.repository.TeamRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final RegistrationRepository registrationRepository;
    private final MatchRepository matchRepository;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper, RegistrationRepository registrationRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.registrationRepository = registrationRepository;
        this.matchRepository = matchRepository;
    }


    //--------- funcoes auxiliares -------------
    private void checkNameAvailable(String name, Long excludedId) {
        boolean exists = (excludedId == null)
                ? teamRepository.existsByName(name)
                : teamRepository.existsByNameAndIdNot(name, excludedId);
        if (exists) {
            throw new BusinessRuleException("Já existe uma equipe com o nome '" + name + "'");
        }
    }

    private void checkAcronymAvailable(String acronym, Long excludedId) {
        boolean exists = (excludedId == null)
                ? teamRepository.existsByTeamAcronym(acronym)
                : teamRepository.existsByTeamAcronymAndIdNot(acronym, excludedId);
        if (exists) {
            throw new BusinessRuleException("Já existe uma equipe com a sigla '" + acronym + "'");
        }
    }

    public Team findEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe não encontrada com id " + id));
    }

    //funcoes essenciais

    @Transactional
    public TeamResponse createTeam(TeamRequest request){
        Team team = teamMapper.toEntity(request);
        checkNameAvailable(team.getName(), null);
        checkAcronymAvailable(team.getTeamAcronym(), null);

        return teamMapper.toResponse(teamRepository.save(team));

    }

    @Transactional(readOnly = true)
    public List<TeamResponse> findAllTeams() {
        return teamMapper.toResponseList(teamRepository.findAll(Sort.by("name")));
    }

    @Transactional(readOnly = true)
    public TeamResponse findById(Long id) {
        return teamMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public TeamResponse update(Long id, TeamRequest request) {
        Team team = findEntityById(id);

        Team normalized = teamMapper.toEntity(request);
        checkNameAvailable(normalized.getName(), id);
        checkAcronymAvailable(normalized.getTeamAcronym(), id);

        teamMapper.updateEntity(request, team);
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Transactional
    public void delete(Long id) {
        Team team = findEntityById(id);

        if (registrationRepository.existsByTeamId(id) || matchRepository.existsByHomeTeamIdOrAwayTeamId(id, id)) {
            throw new BusinessRuleException("Não é possível excluir a equipe: ela possui inscrições ou partidas");
        }

        teamRepository.delete(team);
    }


}


