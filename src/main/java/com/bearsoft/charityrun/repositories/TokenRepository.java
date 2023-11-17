package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
        SELECT t FROM Token t INNER JOIN t.appUser au 
        WHERE au.id = :userId AND t.expired = false AND t.revoked = false
        """)
    List<Token> findAllValidTokensByUser(Long userId);
    Optional<Token> findByToken(String token);
    void deleteByAppUserId(Long appUserId);
}
