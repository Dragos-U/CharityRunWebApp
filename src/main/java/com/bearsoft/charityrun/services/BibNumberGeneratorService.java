package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.enums.CourseType;

public interface BibNumberGeneratorService {

    int generateBibNumber(CourseType courseType);
}
