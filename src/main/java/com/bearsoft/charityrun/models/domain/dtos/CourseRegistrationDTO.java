package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRegistrationDTO {

    @NotNull(message = "The T-Shirt-size must not be null.")
    private TShirtSize tShirtSize;

    @NotNull(message = "The Course-Type must not be null.")
    private CourseType courseType;
}
