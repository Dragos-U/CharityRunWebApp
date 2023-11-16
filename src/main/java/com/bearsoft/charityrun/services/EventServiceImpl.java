package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.exceptions.event.EventAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.event.EventNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventUpdateException;
import com.bearsoft.charityrun.models.dtos.EventDTO;
import com.bearsoft.charityrun.models.entities.Event;
import com.bearsoft.charityrun.repositories.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        LocalDate date = eventDTO.getDate();
        if (eventRepository.existsByDate(date)) {
            throw new EventAlreadyExistsException("Event already exists for date: " + date);
        }
        Event event = objectMapper.convertValue(eventDTO, Event.class);
        eventRepository.save(event);
        return eventDTO;
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        try {
            Event updatedEvent = eventRepository.findById(id)
                    .map(event -> updateEventDetails(event, eventDTO))
                    .orElseThrow(() -> new EventNotFoundException("Event with id: " + id + " not found."));
            Event savedEvent = eventRepository.save(updatedEvent);
            return convertToDTO(savedEvent);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new EventUpdateException("Failed to update event with id: " + id, e);
        }
    }

    @Override
    public EventDTO getEventById(Long eventID) {
        try {
            return eventRepository
                    .getEventById(eventID)
                    .map(this::convertToDTO)
                    .orElseThrow(() -> new EventNotFoundException("Event with id: " + eventID + " not found."));
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteEvent(Long eventID, String deleteApproval) {
        if (deleteApproval.equals("true")) {
            try {
                eventRepository
                        .getEventById(eventID)
                        .orElseThrow(() -> new EventNotFoundException("Event with id: " + eventID + " not found."));
                eventRepository.deleteById(eventID);
                return true;
            } catch (EventNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            }
        }
        return false;
    }

    public Event updateEventDetails(Event event, EventDTO eventDTO) {
        event.setName(eventDTO.getName());
        event.setVenue(eventDTO.getVenue());
        event.setDate(eventDTO.getDate());
        event.setCourses(eventDTO.getCourses());
        return event;
    }

    private EventDTO convertToDTO(Event event) {
        return objectMapper.convertValue(event, EventDTO.class);
    }
}
