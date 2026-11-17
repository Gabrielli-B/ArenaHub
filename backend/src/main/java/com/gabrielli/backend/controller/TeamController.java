package com.gabrielli.backend.controller;

import com.gabrielli.backend.dto.TeamRequest;
import com.gabrielli.backend.dto.TeamResponse;
import com.gabrielli.backend.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arenahub/teams")

public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse createTeam(@Valid @RequestBody TeamRequest request){return teamService.createTeam(request);}

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TeamResponse> findAllTeams(){return teamService.findAllTeams();}

    @GetMapping("/{id}")
    public TeamResponse findById(@PathVariable Long id){return teamService.findById(id);}

    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable Long id, @Valid @RequestBody TeamRequest request){return teamService.update(id, request);}

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Long id){teamService.delete(id);}
}
