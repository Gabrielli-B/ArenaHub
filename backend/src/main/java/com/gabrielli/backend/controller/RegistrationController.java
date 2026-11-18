package com.gabrielli.backend.controller;

import com.gabrielli.backend.dto.RegistrationRequest;
import com.gabrielli.backend.dto.RegistrationResponse;
import com.gabrielli.backend.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arenahub/registrations")

public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/tournament/{tournamentId}")
    @ResponseStatus(HttpStatus.CREATED)
    RegistrationResponse registerTeam(@PathVariable long tournamentId, @Valid @RequestBody RegistrationRequest request) {
        return registrationService.registerTeam(tournamentId,request);
    }

    @GetMapping("/{id}")
    List<RegistrationResponse> findByTournament(@PathVariable Long id) {return registrationService.findByTournament(id);}

    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    public void removeTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {registrationService.removeTeam(tournamentId,teamId);}

}
