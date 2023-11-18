package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByDate(LocalDate date);

    @Modifying
    @Query("""
        DELETE FROM Event e WHERE e.id = :id
            """)
    void deleteById(Long id);
    Optional<Event> getEventById(Long id);
}
