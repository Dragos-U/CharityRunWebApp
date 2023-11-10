package com.bearsoft.charityrun.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String venue;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @OneToMany(mappedBy = "event")
    private List<Course> courses;

}
