package com.bearsoft.charityrun.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String city;

    @Column(name="postal_code")
    private String postalCode;

    @OneToOne(mappedBy = "address")
    @JsonBackReference
    private AppUser appUser;
}
