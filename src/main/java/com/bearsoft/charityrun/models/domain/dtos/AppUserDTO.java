package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.entities.Address;
import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import com.bearsoft.charityrun.models.validation.OnCreate;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
public class AppUserDTO {

    @NotEmpty(message = "User's FIRST NAME must not be empty.", groups = OnCreate.class)
    private String firstName;

    @NotEmpty(message = "User's LAST NAME must not be empty.", groups = OnCreate.class)
    private String lastName;

    @NotEmpty(message = "User's EMAIL NAME must not be empty.", groups = OnCreate.class)
    private String email;

    @NotEmpty(message = "User's PASSWORD must not be empty.", groups = OnCreate.class)
    private String password;
    private Address address;
    private CourseRegistration courseRegistration;
}
