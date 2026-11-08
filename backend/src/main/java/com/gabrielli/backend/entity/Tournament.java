package com.gabrielli.backend.entity;

import com.gabrielli.backend.enums.Modality;
import com.gabrielli.backend.enums.RegistrationStatus;
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
    private Long id;

    @Column(nullable = false, length = 60)
    @Size(min = 3,max = 60, message = "O nome do torneio deve ter entre 3 a 60 caracteres")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Modality modality;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus registrationStatus;

    @Column(nullable = false)
    @Min(value = 4, message = "O limite mínimo deve ser de 4 equipes")
    @Max(value = 10, message = "O limite máximo deve ser de 10 equipes")
    private Integer teamLimit;

    private int registrationCount=0;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
