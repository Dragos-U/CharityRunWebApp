package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.OpenAiRequestDTO;
import com.bearsoft.charityrun.services.models.interfaces.TrainingPlanGeneratorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/course-registrations/training-plans")
@RequiredArgsConstructor
@Tag(name = "Training Plan")
public class TrainingPlanController {

    private final TrainingPlanGeneratorService trainingPlanGeneratorService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_PARTICIPANT')")
    public ResponseEntity<String> createTrainingPlan(
            @RequestBody OpenAiRequestDTO openAiRequestDTO,
            Principal connectedAppUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingPlanGeneratorService
                        .generateTrainingPlan(openAiRequestDTO, connectedAppUser));
    }
}
