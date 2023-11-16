package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;

@Data
@Builder
public class CourseDTO {

    private Long eventID;
    private CourseType courseType;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
}
