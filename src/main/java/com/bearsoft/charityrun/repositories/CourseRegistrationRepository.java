package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {

    @Query("""
          SELECT cr.bib FROM CourseRegistration cr
            """)
    Set<Integer> findAllBibNumbers();
}