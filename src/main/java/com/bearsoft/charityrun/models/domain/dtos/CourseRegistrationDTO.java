package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRegistrationDTO {

    private TShirtSize tshirtSize;
    private CourseType courseType;
}
