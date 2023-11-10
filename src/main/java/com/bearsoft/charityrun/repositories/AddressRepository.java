package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
