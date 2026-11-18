package com.gabrielli.backend.enums;

import lombok.Getter;

@Getter
public enum TournamentStatus {
    OPEN("INSCRIÇÕES ABERTAS"),
    IN_PROGRESS("EM ANDAMENTO"),
    FINISHED("FINALIZADO"),
    CANCELED("CANCELADO");

    private final String description;

    TournamentStatus(String description) {
        this.description = description;
    }
}

