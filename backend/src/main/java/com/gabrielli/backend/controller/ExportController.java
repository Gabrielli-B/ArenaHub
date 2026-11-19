package com.gabrielli.backend.controller;

import com.gabrielli.backend.dto.TeamResponse;
import com.gabrielli.backend.dto.TournamentExportResponse;
import com.gabrielli.backend.service.ExportService;
import com.gabrielli.backend.service.TournamentService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

@RestController
@RequestMapping("/arenahub/export")
public class ExportController {

    private final ExportService exportService;
    private final TournamentService tournamentService;

    public ExportController(ExportService exportService, TournamentService tournamentService) {
        this.exportService = exportService;
        this.tournamentService = tournamentService;
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<TournamentExportResponse> exportTournament(@PathVariable Long tournamentId) {
        TournamentExportResponse body = exportService.exportTournament(tournamentId);
        String fileName = "torneio-" + slug(body.tournament().name()) + ".json";
        return asDownload(body, fileName);
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> exportTeams() {return asDownload(exportService.exportTeams(), "equipes.json");}

    private <T> ResponseEntity<T> asDownload(T body, String fileName) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private String slug(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? "torneio" : normalized;
    }
}
