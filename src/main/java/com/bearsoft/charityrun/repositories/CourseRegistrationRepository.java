package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {

}
