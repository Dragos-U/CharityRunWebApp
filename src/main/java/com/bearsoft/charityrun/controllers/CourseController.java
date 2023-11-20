package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_PARTICIPANT')")
    public ResponseEntity<List<CourseDTO>> getAllCourses(
            @RequestParam(required = false, defaultValue = "1") Long eventID
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getCourseByEventId(eventID));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestParam(required = false, defaultValue = "1") Long eventID,
            @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(courseDTO, eventID));
    }

    @PatchMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseDTO> updateCourseStartTime(
            @RequestParam(required = false, defaultValue = "1") Long eventID,
            @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(courseService.updateCourseStartTime(courseDTO, eventID));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCourseByType(
            @RequestParam(required = true, defaultValue = "1") Long eventID,
            @RequestParam(required = true) CourseType courseType,
            @RequestParam(required = true, defaultValue = "false") String deleteApproval) {
        boolean isCourseDeleted = courseService.deleteEventCourseByType(eventID, courseType, deleteApproval);
        return ResponseEntity.status(HttpStatus.OK).body("Event deleted: " + isCourseDeleted);
    }
}