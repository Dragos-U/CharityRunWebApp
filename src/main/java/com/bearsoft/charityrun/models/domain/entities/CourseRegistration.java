package com.bearsoft.charityrun.models.domain.entities;

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
    private int bib;

    @Column(name = "t_shirt_size")
    @Enumerated(EnumType.STRING)
    private TShirtSize tshirtSize;

    @OneToMany (mappedBy = "courseRegistration",fetch = FetchType.EAGER )
    @JsonManagedReference
    private List<TrainingPlan> trainingPlans;

    @OneToOne(mappedBy = "courseRegistration")
    @JsonBackReference
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;
}
