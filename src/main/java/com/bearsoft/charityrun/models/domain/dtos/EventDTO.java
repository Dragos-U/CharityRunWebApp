package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.entities.Course;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {

    private String name;
    private String venue;
    private LocalDate date;
    private List<Course> courses;
}
