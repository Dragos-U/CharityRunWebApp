package com.bearsoft.charityrun.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="course_registration")
public class CourseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int bib;

    @Column(name = "t_shirt_size")
    private char tShirtSize;

    @OneToMany (mappedBy = "courseRegistration")
    private List<TrainingPlan> trainingPlans;

    @OneToOne(mappedBy = "courseRegistration")
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    private Course course;
}
