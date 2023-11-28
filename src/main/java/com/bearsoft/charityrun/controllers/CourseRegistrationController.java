package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.services.CourseRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/course-registrations")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CourseRegistrationDTO> registerLoggedAppUserToCourse(
            @RequestBody CourseRegistrationDTO courseRegistrationDTO,
            Principal connectedAppUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseRegistrationService
                        .registerLoggedAppUserToCourse(courseRegistrationDTO, connectedAppUser));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_PARTICIPANT')")
    public ResponseEntity<String> unregisterLoggedAppUserFromCourse(Principal connectedAppUser) {
        courseRegistrationService.unregisterLoggedAppUserFromCourse(connectedAppUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Participant successfully unregistered.");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PARTICIPANT')")
    public ResponseEntity<CourseRegistrationDTO> getLoggedAppUserRegistration(Principal connectedAppUser) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(courseRegistrationService
                        .getLoggedAppUserRegistration(connectedAppUser));
    }
}
