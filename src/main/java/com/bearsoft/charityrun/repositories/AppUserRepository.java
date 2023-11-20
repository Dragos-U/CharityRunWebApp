package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("""
               SELECT au FROM AppUser au WHERE au.email = :email
            """)
    Optional<AppUser> findAppUsersByEmail(String email);

    @Query("SELECT au FROM AppUser au")
    Optional<List<AppUser>> findAllUsers();

}
