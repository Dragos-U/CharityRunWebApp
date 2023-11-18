package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.Course;
import com.bearsoft.charityrun.models.domain.entities.TrainingPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDTO {

    private int bib;
    private char tShirtSize;
    private List<TrainingPlan> trainingPlans;
    private AppUser appUser;
    private Course course;
}
