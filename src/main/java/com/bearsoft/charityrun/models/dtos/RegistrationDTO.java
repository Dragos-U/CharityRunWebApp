package com.bearsoft.charityrun.models.dtos;

import com.bearsoft.charityrun.models.entities.AppUser;
import com.bearsoft.charityrun.models.entities.Course;
import com.bearsoft.charityrun.models.entities.TrainingPlan;
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
