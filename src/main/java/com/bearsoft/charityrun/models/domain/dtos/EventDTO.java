package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.entities.Course;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {

    @NotEmpty(message = "Event's NAME must not be empty.")
    private String name;

    @NotEmpty(message = "Event's VENUE must not be empty.")
    private String venue;

    @NotNull(message = "Event's DATE must not be null.")
    @Future(message = "Event date must be set in the future.")
    private LocalDate date;

    private List<Course> courses;
}
