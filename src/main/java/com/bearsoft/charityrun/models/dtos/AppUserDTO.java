package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.entities.Address;
import com.bearsoft.charityrun.models.entities.Role;
import lombok.*;

import java.util.Set;

@Data
@Builder
public class AppUserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Address address;
    //private Set<Role> roles;
}
