package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.enums.CourseType;

import java.util.List;

public interface CourseService {

    CourseDTO createCourse(CourseDTO courseDTO, Long eventID);
    CourseDTO updateCourseStartTime(CourseDTO courseDTO, Long eventID);
    List<CourseDTO> getCourseByEventId(Long eventID);
    void deleteEventCourseByType(Long eventID, CourseType courseType, String deleteApproval);
}
