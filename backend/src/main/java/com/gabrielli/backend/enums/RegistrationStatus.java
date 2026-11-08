package com.gabrielli.backend.enums;

public enum RegistrationStatus {
    IN_PROGRESS("EM PROGRESSO"),
    COMPLETED("COMPLETA"),
    CANCELLED("CANCELADA");

    private String description;

    RegistrationStatus(String description) {
        this.description = description;
    }
}
