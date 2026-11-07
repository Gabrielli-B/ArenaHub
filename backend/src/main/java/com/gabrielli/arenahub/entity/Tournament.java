package com.gabrielli.arenahub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 60)
    @Size(min = 3,max = 60, message = "O nome do torneio deve ter entre 3 a 60 caracteres")
    private String name;

    private String modality;

    //Status status;

    @Column(nullable = false)
    @Min(value = 4, message = "O lime mínimo deve ser de 4 participantes")
    @Max(value = 10, message = "O lime máximo deve ser de 10 participantes")
    private int teamLimit;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
