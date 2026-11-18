package com.gabrielli.backend.repository;

import com.gabrielli.backend.entity.Registration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {

    boolean existsByTeamId(Long teamId);

    boolean existsByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    void deleteByTournamentId(Long tournamentId);

    long countByTournamentId(Long tournamentId);

    Optional<Registration> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);

    @EntityGraph(attributePaths = "team")
    List<Registration> findByTournamentIdOrderByTeamNameAsc(Long tournamentId);


}
