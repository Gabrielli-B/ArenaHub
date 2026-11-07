package com.gabrielli.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teams")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 60)
    @Size(min = 3,max = 60, message = "O nome deve ter entre 3 a 60 caracteres")
    private String name;

    @Column(nullable = false,unique = true,length = 5)
    @Size(min = 2, max = 5, message = "A sigla deve ter entre 2 a 5 caracteres")
    private String teamAcronym;

    private String city;
}

