package com.gabrielli.backend.enums;

public enum MatchStatus {
    SCHEDULED("AGENDADO"),
    IN_PROGRESS("EM PROGRESSO"),
    FINISHED("FINALIZADO"),
    CANCELED("CANCELADO");

    private String description;

    MatchStatus(String description) {
        this.description = description;
    }
}
