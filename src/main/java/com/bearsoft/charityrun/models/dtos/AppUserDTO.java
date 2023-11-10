package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.entities.Address;
import com.bearsoft.charityrun.models.entities.Registration;
import com.bearsoft.charityrun.models.entities.Role;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
    private Set<Registration> registrations;
    private Set<Role> roles = new HashSet<>();
}
