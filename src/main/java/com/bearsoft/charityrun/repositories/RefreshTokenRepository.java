package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByAppUserId(Long appUserId);
}
