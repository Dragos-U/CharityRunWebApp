package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.GenderType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponseDTO {

    private String firstName;
    private String lastName;
    private int age;
    private GenderType gender;
    private int bib;
}
