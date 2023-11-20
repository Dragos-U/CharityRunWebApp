package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Data
@Builder
public class CourseDTO {

    private Long eventID;

    @NotNull(message = "Course TYPE must not be null.")
    private CourseType courseType;

    @NotNull(message = "Course START TIME must not be null.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
}
