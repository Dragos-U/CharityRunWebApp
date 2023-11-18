package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.dtos.EventDTO;

public interface EventService {

    EventDTO createEvent(EventDTO eventDTO);
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    EventDTO getEventById(Long id);
    boolean deleteEvent(Long id, String deleteCode);
}
