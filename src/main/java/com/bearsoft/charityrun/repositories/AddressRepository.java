package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
