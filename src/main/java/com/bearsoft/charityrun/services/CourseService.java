package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.enums.CourseType;

import java.util.List;

public interface CourseService {

    CourseDTO createCourse(CourseDTO courseDTO);
    CourseDTO updateCourseStartTime(CourseDTO courseDTO);
    List<CourseDTO> getAllCourses();
    boolean deleteCourse(CourseType courseType, String deleteApproval);
}
