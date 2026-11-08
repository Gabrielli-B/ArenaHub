package com.gabrielli.backend.repository;

import com.gabrielli.backend.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match,Long> {

}
