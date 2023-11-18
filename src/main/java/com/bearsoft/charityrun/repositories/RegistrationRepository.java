package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<CourseRegistration, Long> {
}
