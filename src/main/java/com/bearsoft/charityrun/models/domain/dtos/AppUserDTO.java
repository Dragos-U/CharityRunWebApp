package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.entities.Address;
import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import lombok.*;

@Data
@Builder
public class AppUserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Address address;
    private CourseRegistration courseRegistration;
}
