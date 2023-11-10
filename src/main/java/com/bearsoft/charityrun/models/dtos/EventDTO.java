package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.entities.Course;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {

    private Long id;
    private String name;
    private String venue;
    private LocalDate date;
    private List<Course> courses;
}
