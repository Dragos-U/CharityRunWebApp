package com.bearsoft.charityrun.services.models.interfaces;

import com.bearsoft.charityrun.models.domain.enums.CourseType;

public interface BibNumberGeneratorService {

    int generateBibNumber(CourseType courseType);
}
