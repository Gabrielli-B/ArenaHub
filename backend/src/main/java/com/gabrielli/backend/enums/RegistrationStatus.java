package com.gabrielli.backend.enums;

import lombok.Getter;

@Getter
public enum RegistrationStatus {
    OPEN("ABERTA"),
    CLOSED("ENCERRADA"),
    CANCELLED("CANCELADA");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }
}
