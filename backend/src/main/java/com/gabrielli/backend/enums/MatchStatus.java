package com.gabrielli.backend.enums;

import lombok.Getter;

@Getter
public enum MatchStatus {
    SCHEDULED("AGENDADO"),
    IN_PROGRESS("EM PROGRESSO"),
    FINISHED("FINALIZADO"),
    CANCELED("CANCELADO");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }
}
