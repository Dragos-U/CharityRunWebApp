package com.bearsoft.charityrun.models.domain.entities;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    @Column(name = "start_time")
    private LocalTime startTime;

    @OneToMany(mappedBy = "course")
    @JsonManagedReference("course-courseRegistration")
    private Set<CourseRegistration> courseRegistrations;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference("event-courses")
    private Event event;

}