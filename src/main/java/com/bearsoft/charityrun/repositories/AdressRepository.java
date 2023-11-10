package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdressRepository extends JpaRepository<Address, Long> {
}
