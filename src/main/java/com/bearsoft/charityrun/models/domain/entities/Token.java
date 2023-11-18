package com.bearsoft.charityrun.models.domain.entities;

import com.bearsoft.charityrun.models.domain.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name="app_user_id")
    @JsonBackReference("appUser-tokens")
    private AppUser appUser;
}
