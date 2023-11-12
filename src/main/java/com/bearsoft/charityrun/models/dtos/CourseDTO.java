package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.entities.Event;
import com.bearsoft.charityrun.models.entities.Registration;
import com.bearsoft.charityrun.models.enums.CourseType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class CourseDTO {

    private Long id;
    private CourseType courseType;
    private LocalDateTime startTime;
    private Set<Registration> registrations;
    private Event event;
}
