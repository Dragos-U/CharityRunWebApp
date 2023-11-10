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
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int bib;

    @Column(name = "t_shirt_size")
    private char tShirtSize;

    @OneToMany (mappedBy = "registration")
    private List<TrainingPlan> trainingPlans;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    private Course course;
}
