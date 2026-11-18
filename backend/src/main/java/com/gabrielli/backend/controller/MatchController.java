package com.gabrielli.backend.controller;

import com.gabrielli.backend.dto.ClassificationResponse;
import com.gabrielli.backend.dto.MatchRequest;
import com.gabrielli.backend.dto.MatchResponse;
import com.gabrielli.backend.service.MatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arenahub/matches")

public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {this.matchService = matchService;}

    @PostMapping("/tournament/{tournamentId}/start")
    public List<MatchResponse> startTournament(@PathVariable Long tournamentId) {return matchService.startTournament(tournamentId);}

    @GetMapping("/tournament/{tournamentId}")
    public List<MatchResponse> findMatchesByTournament(@PathVariable Long tournamentId, @RequestParam(required = false) Integer round) {
        return matchService.findMatchesByTournament(tournamentId, round);
    }

    @PutMapping("/{matchId}/result")
    public MatchResponse registerResult(@PathVariable Long matchId, @RequestBody MatchRequest request) {return matchService.registerResult(matchId, request);}

    @GetMapping("/tournament/{tournamentId}/classification")
    public List<ClassificationResponse> getClassification(@PathVariable Long tournamentId) {return matchService.getClassification(tournamentId);}


}
