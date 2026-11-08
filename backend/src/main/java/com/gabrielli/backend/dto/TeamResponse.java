package com.gabrielli.backend.dto;

//apenas saída
public record TeamResponse(
        Long id,
        String name,
        String teamAcronym,
        String city
) { }
