package com.bearsoft.charityrun.models.entities;

import com.bearsoft.charityrun.models.enums.CourseType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime startTime;

    @OneToMany(mappedBy = "course")
    private Set<Registration> registrations;

    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;

    public double getCourseLength() {
        return courseType.getCourseLength();
    }
}
