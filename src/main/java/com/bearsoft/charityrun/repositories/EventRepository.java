package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
