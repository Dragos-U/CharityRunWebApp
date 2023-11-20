package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.domain.entities.Course;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
                SELECT c FROM Course c WHERE c.event.id =:eventID
            """)
    List<Course> findAllCoursesByEventId(Long eventID);

    @Query("""
            SELECT c FROM Course c WHERE c.courseType = :courseType
            """)
    Optional<Course> getCourseByCourseType(CourseType courseType);
    @Query("""
                    SELECT c from Course c WHERE c.courseType = :courseType AND c.event.id = :eventID
            """)
    Optional<Course> getCourseByCourseTypeAndEventId(Long eventID,CourseType courseType);

    boolean existsByCourseType(CourseType courseType);

    @Modifying
    @Query("""
                DELETE FROM Course c WHERE c.courseType =:courseType
            """)
    void deleteByCourseType(CourseType courseType);

}
