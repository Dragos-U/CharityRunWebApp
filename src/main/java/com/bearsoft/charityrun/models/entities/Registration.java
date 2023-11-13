package com.bearsoft.charityrun.models.entities;

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
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int bib;

    @Column(name = "t_shirt_size")
    private char tShirtSize;

    @OneToMany (mappedBy = "registration")
    @JsonManagedReference
    private List<TrainingPlan> trainingPlans;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "app_user_id")
    @JsonBackReference
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;
}
