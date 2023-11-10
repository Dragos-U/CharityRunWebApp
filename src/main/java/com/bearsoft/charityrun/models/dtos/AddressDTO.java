package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.entities.AppUser;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {

    private Long id;
    private String country;
    private String city;
    private String postalCode;
    private AppUser appUser;
}
