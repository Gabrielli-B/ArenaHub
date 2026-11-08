package com.gabrielli.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TeamRequest(
        @NotBlank(message = "O nome da equipe é obrigatório")
        @Size(min = 3,max = 60, message = "O nome deve ter entre 3 a 60 caracteres")
        String name,

        @NotBlank(message = "A sigla da equipe é obrigatório")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "A sigla deve conter apenas letras")
        @Size(min = 2, max = 5, message = "A sigla deve ter entre 2 a 5 caracteres")
        String teamAcronym,

        String city
) { }
