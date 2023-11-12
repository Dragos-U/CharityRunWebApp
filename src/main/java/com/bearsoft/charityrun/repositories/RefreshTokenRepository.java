package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
