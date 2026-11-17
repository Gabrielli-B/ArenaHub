package com.gabrielli.backend.repository;

import com.gabrielli.backend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team,Long> {

    boolean existsByName(String name);
    boolean existsByTeamAcronym(String teamAcronym);

    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByTeamAcronymAndIdNot(String teamAcronym, Long id);

}
