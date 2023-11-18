package com.bearsoft.charityrun.models.domain.entities;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Set<CourseRegistration> courseRegistrations;

    @ManyToOne
    @JoinColumn(name="event_id")
    @JsonBackReference
    private Event event;

    public double getCourseLength() {
        return courseType.getCourseLength();
    }
}
