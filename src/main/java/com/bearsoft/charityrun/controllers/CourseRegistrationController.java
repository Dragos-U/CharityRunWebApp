package com.bearsoft.charityrun.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/course-registrations")
@RequiredArgsConstructor
public class CourseRegistrationController {

    @PostMapping
    public ResponseEntity<String> registerParticipantToCourse(){
        return null;
    }

    @DeleteMapping
    public ResponseEntity<String> unregisterParticipantFromCourse(){
        return null;
    }
}
