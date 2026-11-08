package com.gabrielli.backend.dto;

public record ClassificationResponse(
        int position,
        TeamResponse team,
        int points,
        int games,
        int wins,
        int draws,
        int losses,
        int scoreFor,
        int scoreAgainst,
        int scoreDifference
) {
}
