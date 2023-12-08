package com.bearsoft.charityrun.services.models.interfaces;

import com.bearsoft.charityrun.models.domain.dtos.OpenAiRequestDTO;

import java.security.Principal;

public interface TrainingPlanGeneratorService {

    String generateTrainingPlan(OpenAiRequestDTO openAiRequestDTO, Principal connectedAppUser);
}
