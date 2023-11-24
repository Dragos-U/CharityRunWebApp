package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
import com.bearsoft.charityrun.validators.ValidEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRegistrationDTO {

    @ValidEnum(message = "The T-Shirt-size is invalid.")
    private TShirtSize tShirtSize;

    @ValidEnum(message = "The Course-Type is invalid.")
    private CourseType courseType;
}
