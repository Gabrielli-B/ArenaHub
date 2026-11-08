package com.gabrielli.backend.enums;

import lombok.Getter;

@Getter
public enum Modality {
    VOLLEYBALL("VÔLEI"),
    SOCCER("FUTEBOL"),
    FUTSAL("FUTSAL"),
    BASKETBALL("BASQUETE"),
    CANASTA("CANASTRA"),
    TRUCO("TRUCO"),
    UNO("UNO"),
    CHESS("XADREZ"),
    CHECKERS("DAMAS");

    private final String description;

    Modality(String description) {
        this.description = description;
    }

}
