package com.bearsoft.charityrun.repositories;

import com.bearsoft.charityrun.models.entities.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
}
