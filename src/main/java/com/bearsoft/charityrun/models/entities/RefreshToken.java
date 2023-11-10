package com.bearsoft.charityrun.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id")
    private AppUser appUser;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name= "expiry_date", nullable = false)
    private Instant expiryDate;

}
