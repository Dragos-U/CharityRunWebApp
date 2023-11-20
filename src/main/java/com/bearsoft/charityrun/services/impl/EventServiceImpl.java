package com.bearsoft.charityrun.services.impl;

import com.bearsoft.charityrun.exceptions.event.EventAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.event.EventNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventUpdateException;
import com.bearsoft.charityrun.models.domain.dtos.EventDTO;
import com.bearsoft.charityrun.models.domain.entities.Event;
import com.bearsoft.charityrun.repositories.EventRepository;
import com.bearsoft.charityrun.services.EventService;
import com.bearsoft.charityrun.validator.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final ObjectsValidator<EventDTO> eventDTOObjectsValidator;

    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        eventDTOObjectsValidator.validate(eventDTO);
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
    public EventDTO updateEventByID(Long eventID, EventDTO eventDTO) {
        eventDTOObjectsValidator.validate(eventDTO);
        try {
            Event updatedEvent = eventRepository.findById(eventID)
                    .map(event -> updateEventDetails(event, eventDTO))
                    .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %s not found",eventID)));
            Event savedEvent = eventRepository.save(updatedEvent);
            return convertToDTO(savedEvent);
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            throw new EventUpdateException("Failed to update event with id: " + eventID, e);
        }
    }

    @Override
    public EventDTO getEventById(Long eventID) {
        try {
            return eventRepository
                    .getEventById(eventID)
                    .map(this::convertToDTO)
                    .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %s not found",eventID)));
        } catch (EventNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public String deleteEvent(Long eventID, String deleteApproval) {
        if (deleteApproval.equals("true")) {
            try {
                eventRepository
                        .getEventById(eventID)
                        .orElseThrow(() -> new EventNotFoundException(String.format("Event with id: %s not found",eventID)));
                eventRepository.deleteById(eventID);
                return String.format("Event with id: %d was deleted",eventID);
            } catch (EventNotFoundException e) {
                throw new EventNotFoundException(String.format("Event with id: %s not found",eventID));
            }
        }
        return null;
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
