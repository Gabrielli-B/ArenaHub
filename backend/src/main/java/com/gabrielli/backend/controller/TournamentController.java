package com.gabrielli.backend.controller;

import com.gabrielli.backend.dto.TournamentRequest;
import com.gabrielli.backend.dto.TournamentResponse;
import com.gabrielli.backend.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arenahub/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TournamentResponse createTournament(@Valid @RequestBody TournamentRequest request) {return tournamentService.createTournament(request);}

    @GetMapping
    public List<TournamentResponse> findAllTournaments() {return tournamentService.findAllTournaments();}

    @GetMapping("/{id}")
    public TournamentResponse findById(@PathVariable Long id) {return tournamentService.findById(id);}

    @PutMapping("/{id}")
    public TournamentResponse update(@PathVariable Long id, @Valid @RequestBody TournamentRequest request) {return tournamentService.update(id, request);}

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTournament(@PathVariable Long id) {tournamentService.delete(id);}
}
