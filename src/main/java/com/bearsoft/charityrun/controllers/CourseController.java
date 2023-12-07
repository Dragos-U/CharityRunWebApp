package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.CourseDTO;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.services.models.interfaces.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/courses")
@Tag(name = "Course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @RequestParam(required = false, defaultValue = "1") Long eventID,
            @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(courseDTO, eventID));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_PARTICIPANT')")
    public ResponseEntity<List<CourseDTO>> getAllCourses(
            @RequestParam(required = false, defaultValue = "1") Long eventID
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getCourseByEventId(eventID));
    }

    @PatchMapping()
    public ResponseEntity<CourseDTO> updateCourseStartTime(
            @RequestParam(required = false, defaultValue = "1") Long eventID,
            @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(courseService.updateCourseStartTime(courseDTO, eventID));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCourseByType(
            @RequestParam(required = true, defaultValue = "1") Long eventID,
            @RequestParam(required = true) CourseType courseType,
            @RequestParam(required = true, defaultValue = "false") String deleteApproval) {
        courseService.deleteEventCourseByType(eventID, courseType, deleteApproval);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
