package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
