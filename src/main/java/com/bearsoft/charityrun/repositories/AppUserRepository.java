package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("""
               SELECT au FROM AppUser au WHERE au.email = :email
            """)
    Optional<AppUser> findAppUsersByEmail(String email);

    @Query("""
                    SELECT au FROM AppUser au WHERE au.courseRegistration.course.event.id = :eventId
            """)
    Page<AppUser> findAppUsersByEventId(Long eventId, Pageable pageable);

    @Query("""
            SELECT au FROM AppUser au
            """)
    Optional<List<AppUser>> findAllUsers();

    @Query("""
                SELECT au FROM AppUser au WHERE au.courseRegistration.id != null
            """)
    List<AppUser> findAllRegisteredUsers();

    @Query("SELECT u FROM AppUser u WHERE u.courseRegistration.course.courseType = :courseType " +
            "AND u.courseRegistration.course.event.id = :eventId " +
            "AND u.courseRegistration.gender = :gender " +
            "AND (:minAge IS NULL OR u.courseRegistration.age >= :minAge) " +
            "AND (:maxAge IS NULL OR u.courseRegistration.age <= :maxAge)")
    List<AppUser> findUsersByCourseTypeEventIdAndGender(CourseType courseType, Long eventId, GenderType gender, Integer minAge, Integer maxAge);
}
