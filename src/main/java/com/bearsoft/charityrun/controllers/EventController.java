package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.EventDTO;
import com.bearsoft.charityrun.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @GetMapping("/{eventID}")
    public ResponseEntity<EventDTO> getEventByID(@PathVariable Long eventID){
        EventDTO eventDTO = eventService.getEventById(eventID);
        return ResponseEntity.status(HttpStatus.OK).body(eventDTO);
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventDTO eventDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDTO));
    }

    @PutMapping("/{eventID}")
    public ResponseEntity<String> updateEventByID(
            @PathVariable Long eventID,
            @RequestBody EventDTO eventDTO){
        eventService.updateEvent(eventID, eventDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Event updated.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteEventByID(
            @RequestParam(required = true) Long eventID,
            @RequestParam(required = true, defaultValue = "false") String deleteApproval){
        boolean isEventDeleted = eventService.deleteEvent(eventID, deleteApproval);
        return ResponseEntity.status(HttpStatus.OK).body("Event deleted: "+isEventDeleted);
    }
}
