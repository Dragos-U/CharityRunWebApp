package com.bearsoft.charityrun.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/events")
public class EventController {

    @GetMapping
    public ResponseEntity<String> getEvent(){
        return null;
    }

    @PostMapping
    public ResponseEntity<String> createEvent(){
        return null;
    }

    @PutMapping("/{eventID}")
    public ResponseEntity<String> updateEvent(@PathVariable Long eventID){
        return null;
    }

    @DeleteMapping("/eventID")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventID){
        return null;
    }

}
