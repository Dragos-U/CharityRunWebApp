package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
