package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.EventDTO;
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
    public ResponseEntity<EventDTO> getEventByID(
            @PathVariable Long eventID){
        EventDTO eventDTO = eventService.getEventById(eventID);
        return ResponseEntity.status(HttpStatus.OK).body(eventDTO);
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventDTO eventDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(eventDTO));
    }

    @PutMapping("/{eventID}")
    public ResponseEntity<EventDTO> updateEventByID(
            @PathVariable Long eventID,
            @RequestBody EventDTO eventDTO){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(eventService.updateEventByID(eventID, eventDTO));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteEventByID(
            @RequestParam(required = true) Long eventID,
            @RequestParam(required = true, defaultValue = "false") String deleteApproval){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.deleteEvent(eventID, deleteApproval));
    }
}
