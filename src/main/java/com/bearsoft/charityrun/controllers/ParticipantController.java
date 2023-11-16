package com.bearsoft.charityrun.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ROLE_PARTICIPANT')")
@RequestMapping("/participants")
public class ParticipantController {

    @PostMapping
    public ResponseEntity<String> registerParticipant() {

        return null;
    }

    @GetMapping
    public ResponseEntity<?> getParticipantsForCourse(
            @RequestParam(required = false, defaultValue = "CROSS") String course) {
        return null;

    }

    @GetMapping
    public ResponseEntity<?> getParticipantsWithTShirtSize(
            @RequestParam(required = false, defaultValue = "M") String t_shirt_size) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> sortParticipantsByCourse(
            @RequestParam(required = false, defaultValue = "M") String t_shirt_size) {
        return null;
    }



}
