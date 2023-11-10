package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
