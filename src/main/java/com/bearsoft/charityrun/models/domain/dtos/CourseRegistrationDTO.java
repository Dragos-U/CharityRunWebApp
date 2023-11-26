package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
import com.bearsoft.charityrun.validators.annotations.AgeValidation;
import com.bearsoft.charityrun.validators.annotations.EnumValidation;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRegistrationDTO {

    @EnumValidation
    private TShirtSize tShirtSize;

    @EnumValidation
    private CourseType courseType;

    @EnumValidation
    private GenderType gender;

    @AgeValidation(minAge = 14, message = "Invalid age.")
    private int age;
}
