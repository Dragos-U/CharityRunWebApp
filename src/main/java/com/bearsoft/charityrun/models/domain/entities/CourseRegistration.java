package com.bearsoft.charityrun.models.domain.entities;

import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private int age;
    private int bib;

    @Column(name = "t_shirt_size")
    @Enumerated(EnumType.STRING)
    private TShirtSize tShirtSize;

    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @OneToMany (mappedBy = "courseRegistration",fetch = FetchType.EAGER )
    @JsonManagedReference("courseRegistration-trainingPlan")
    private List<TrainingPlan> trainingPlans;

    @OneToOne(mappedBy = "courseRegistration")
    @JsonBackReference("appUser-courseRegistration")
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    @JsonBackReference("course-courseRegistration")
    private Course course;
}
