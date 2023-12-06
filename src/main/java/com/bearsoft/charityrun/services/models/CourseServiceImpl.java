package com.bearsoft.charityrun.services.models;

import com.bearsoft.charityrun.exceptions.course.CourseAlreadyExistException;
import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventNotFoundException;
import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.entities.Course;
import com.bearsoft.charityrun.models.domain.entities.Event;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.repositories.CourseRepository;
import com.bearsoft.charityrun.repositories.EventRepository;
import com.bearsoft.charityrun.services.models.interfaces.CourseService;
import com.bearsoft.charityrun.validators.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final ObjectsValidator<CourseDTO> courseDTOObjectsValidator;

    @Override
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO, Long eventID) {
        courseDTO.setEventID(eventID);
        courseDTOObjectsValidator.validate(courseDTO);

        Event event = eventRepository
                .getEventById(eventID)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %s not found", eventID)));

        CourseType courseType = courseDTO.getCourseType();
        if (courseRepository.existsByCourseType(courseType)) {
            throw new CourseAlreadyExistException(String.format("Course %s already exists.",courseType));
        }
        Course course = objectMapper.convertValue(courseDTO, Course.class);

        course.setEvent(event);
        log.info("Course was linked to event.");
        courseRepository.save(course);
        log.info("Course was saved to database");
        return courseDTO;
    }

    @Override
    @Transactional
    public CourseDTO updateCourseStartTime(CourseDTO courseDTO, Long eventID) {
        courseDTO.setEventID(eventID);
        courseDTOObjectsValidator.validate(courseDTO);

        eventRepository
                .getEventById(eventID)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %s not found", eventID)));

        CourseType courseType = courseDTO.getCourseType();
        LocalTime startTime = courseDTO.getStartTime();
        Course course = courseRepository.getCourseByCourseTypeAndEventId(eventID, courseType)
                .orElseThrow(() -> new CourseNotFoundException("Course " + courseType + " not found."));
        course.setStartTime(startTime);
        log.info("Course start time updated successfully.");
        courseRepository.save(course);
        return courseDTO;
    }

    @Override
    public List<CourseDTO> getCourseByEventId(Long eventID) {
        eventRepository
                .getEventById(eventID)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %s not found", eventID)));

        return  courseRepository.findAllCoursesByEventId(eventID)
                .stream()
                .map(course -> {
                    CourseDTO courseDTO = objectMapper.convertValue(course, CourseDTO.class);
                    courseDTO.setEventID(course.getEvent().getId());
                    return courseDTO;
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteEventCourseByType(Long eventID, CourseType courseType, String deleteApproval) {
        if (deleteApproval.equals("true")) {
            try {
                courseRepository
                        .getCourseByCourseTypeAndEventId(eventID, courseType)
                        .orElseThrow(() -> new CourseNotFoundException(String.format("Course %s not found.", courseType)));
                courseRepository.deleteByCourseType(courseType);
                log.info("Course deleted successfully.");
            } catch (EventNotFoundException e) {
                log.error("Event not found.");
                throw new EventNotFoundException("Event not found");
            }
        }
    }
}
