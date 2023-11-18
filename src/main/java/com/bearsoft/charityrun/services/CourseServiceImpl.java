package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.exceptions.course.CourseAlreadyExistException;
import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventNotFoundException;
import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.entities.Course;
import com.bearsoft.charityrun.models.domain.entities.Event;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.repositories.CourseRepository;
import com.bearsoft.charityrun.repositories.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Long eventID = 1L;
        Event event = eventRepository
                .getEventById(eventID)
                .orElseThrow(() -> new EventNotFoundException("Event with id: " + eventID + " not found."));

        CourseType courseType = courseDTO.getCourseType();
        if (courseRepository.existsByCourseType(courseType)) {
            throw new CourseAlreadyExistException("Course " + courseType + " already exists.");
        }
        Course course = objectMapper.convertValue(courseDTO, Course.class);

        course.setEvent(event);
        courseRepository.save(course);
        return courseDTO;
    }

    @Override
    @Transactional
    public CourseDTO updateCourseStartTime(CourseDTO courseDTO) {
        CourseType courseType = courseDTO.getCourseType();
        LocalTime startTime = courseDTO.getStartTime();
        Course course = courseRepository.getCourseByCourseType(courseType)
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseType + " not found."));
        course.setStartTime(startTime);

        courseRepository.save(course);
        return objectMapper.convertValue(course, CourseDTO.class);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return  courseRepository.findAllCourses()
                .stream()
                .map(course -> {
                    CourseDTO courseDTO = objectMapper.convertValue(course, CourseDTO.class);
                    if (course.getEvent() != null) {
                        courseDTO.setEventID(course.getEvent().getId());
                    }
                    return courseDTO;
                })
                .toList();
    }

    @Override
    @Transactional
    public boolean deleteCourse(CourseType courseType, String deleteApproval) {
        if (deleteApproval.equals("true")) {
            try {
                courseRepository
                        .getCourseByCourseType(courseType)
                        .orElseThrow(() -> new CourseNotFoundException("Course " + courseType + " not found."));
                courseRepository.deleteByCourseType(courseType);
                return true;
            } catch (EventNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
        }
        return false;
    }
}
