package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Course;
import com.bearsoft.charityrun.models.enums.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
                SELECT c FROM Course c
            """)
    List<Course> findAllCourses();
    Optional<Course> getCourseByCourseType(CourseType courseType);

    boolean existsByCourseType(CourseType courseType);

    @Modifying
    @Query("""
                DELETE FROM Course c WHERE c.courseType =:courseType
            """)
    void deleteByCourseType(CourseType courseType);

}
