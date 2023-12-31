package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.EventDTO;
import com.bearsoft.charityrun.services.models.interfaces.EventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/events")
@Tag(name = "Event")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventDTO eventDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(eventDTO));
    }

    @GetMapping("/{eventID}")
    public ResponseEntity<EventDTO> getEventByID(
            @PathVariable Long eventID) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getEventById(eventID));
    }

    @PutMapping("/{eventID}")
    public ResponseEntity<EventDTO> updateEventByID(
            @PathVariable Long eventID,
            @RequestBody EventDTO eventDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(eventService.updateEventByID(eventID, eventDTO));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEventByID(
            @RequestParam(required = true) Long eventID,
            @RequestParam(required = true, defaultValue = "false") String deleteApproval) {
        eventService.deleteEvent(eventID, deleteApproval);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
