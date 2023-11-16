package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.CourseDTO;
import com.bearsoft.charityrun.models.enums.CourseType;
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
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getAllCourses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(courseDTO));
    }

    @PatchMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CourseDTO> updateCourseStartTime(@RequestBody CourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(courseService.updateCourseStartTime(courseDTO));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCourseByType(
            @RequestParam(required = true) CourseType courseType,
            @RequestParam(required = true, defaultValue = "false") String deleteApproval) {
        boolean isCourseDeleted = courseService.deleteCourse(courseType, deleteApproval);
        return ResponseEntity.status(HttpStatus.OK).body("Event deleted: " + isCourseDeleted);
    }
}
